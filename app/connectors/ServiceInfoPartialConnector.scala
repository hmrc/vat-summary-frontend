/*
 * Copyright 2019 HM Revenue & Customs
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

package connectors

import config.{AppConfig, VatHeaderCarrierForPartialsConverter}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceInfoPartialConnector @Inject()(val http: HttpClient,
                                            hcForPartials: VatHeaderCarrierForPartialsConverter)
                                           (implicit val messagesApi: MessagesApi,
                                            val config: AppConfig) extends HtmlPartialHttpReads with I18nSupport {
  import hcForPartials._

  lazy val btaUrl: String = config.btaBaseUrl + "/business-account/partial/service-info"

  def getServiceInfoPartial()(implicit request: Request[_], executionContext: ExecutionContext): Future[Html] =
    http.GET[HtmlPartial](btaUrl) recover connectionExceptionsAsHtmlPartialFailure map {
      p =>
        p.successfulContentOrElse(views.html.templates.btaNavigationLinks())
    } recover {
      case _ =>
        Logger.warn(s"[ServiceInfoPartialConnector][getServiceInfoPartial] - Unexpected future failed error")
        views.html.templates.btaNavigationLinks()
    }
}
