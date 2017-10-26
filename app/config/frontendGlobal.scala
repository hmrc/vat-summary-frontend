/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.typesafe.config.Config
import config.filters.WhitelistFilter
import connectors.FrontendAuditConnector
import net.ceedubs.ficus.Ficus._
import play.api.mvc.{EssentialFilter, Request}
import play.api.{Application, Configuration, Play}
import play.twirl.api.Html
import uk.gov.hmrc.crypto.ApplicationCrypto
import uk.gov.hmrc.play.config.{AppName, ControllerConfig, RunMode}
import uk.gov.hmrc.play.frontend.bootstrap.DefaultFrontendGlobal
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.play.frontend.filters.{FrontendAuditFilter, FrontendLoggingFilter, MicroserviceFilterSupport, RecoveryFilter}

object FrontendGlobal extends DefaultFrontendGlobal {

  lazy val application: Application = Play.current
  override lazy val auditConnector: FrontendAuditConnector = new FrontendAuditConnector(application)
  override val loggingFilter: LoggingFilter.type = LoggingFilter
  override val frontendAuditFilter: AuditFilter.type = AuditFilter

  override protected lazy val defaultFrontendFilters: Seq[EssentialFilter] = {
    val coreFilters: Seq[EssentialFilter] = super.defaultFrontendFilters.filterNot(f => f.equals(RecoveryFilter))
    val ipWhiteListKey: Boolean = application.configuration.getBoolean("whitelist.enabled").getOrElse(false)

    if(ipWhiteListKey)  {
      coreFilters.:+(new WhitelistFilter(application))
    }
    else {
      coreFilters
    }
  }

  override def onStart(app: Application) {
    super.onStart(app)
    ApplicationCrypto.verifyConfiguration()
  }

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html = {
    val appConfig: AppConfig = application.injector.instanceOf[AppConfig]
    views.html.errors.error_template(appConfig, pageTitle, heading, message)
  }

  override def microserviceMetricsConfig(implicit app: Application): Option[Configuration] = app.configuration.getConfig(s"microservice.metrics")
}

object ControllerConfiguration extends ControllerConfig {
  lazy val controllerConfigs: Config = FrontendGlobal.application.configuration.underlying.as[Config]("controllers")
}

object LoggingFilter extends FrontendLoggingFilter with MicroserviceFilterSupport {
  override def controllerNeedsLogging(controllerName: String): Boolean =
    ControllerConfiguration.paramsForController(controllerName).needsLogging
}

object AuditFilter extends FrontendAuditFilter with RunMode with AppName with MicroserviceFilterSupport {

  override lazy val maskedFormFields: Seq[String] = Seq("password")

  override lazy val applicationPort: None.type = None

  override lazy val auditConnector: FrontendAuditConnector = new FrontendAuditConnector(FrontendGlobal.application)

  override def controllerNeedsAuditing(controllerName: String): Boolean =
    ControllerConfiguration.paramsForController(controllerName).needsAuditing
}
