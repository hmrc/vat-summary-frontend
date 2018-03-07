/*
 * Copyright 2018 HM Revenue & Customs
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

import java.net.URLEncoder
import java.util.Base64
import javax.inject.{Inject, Singleton}

import config.features.Features
import config.{ConfigKeys => Keys}
import play.api.Mode.Mode
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val whitelistEnabled: Boolean
  val whitelistedIps: Seq[String]
  val whitelistExcludedPaths: Seq[Call]
  val shutterPage: String
  val authUrl: String
  val signInUrl: String
  val signInContinueBaseUrl: String
  val features: Features
  val vatApiBaseUrl: String
  val financialDataBaseUrl: String
  val vatSummaryPartial: String
  val vatSubmittedReturnsUrl: String
  val vatReturnDeadlinesUrl: String
  def vatReturnUrl(periodKey: String): String
  val btaHomeUrl: String
  val paymentsServiceUrl: String
  val paymentsReturnUrl: String
  val btaVatOverviewUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, val environment: Environment) extends AppConfig {

  override val mode: Mode = environment.mode

  private lazy val contactHost: String = getString(Keys.contactFrontendService)
  private lazy val contactFormServiceIdentifier: String = "VATVC"

  override lazy val authUrl: String = baseUrl("auth")
  override lazy val analyticsToken: String = getString(Keys.googleAnalyticsToken)
  override lazy val analyticsHost: String = getString(Keys.googleAnalyticsHost)
  override lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private def whitelistConfig(key: String): Seq[String] = Some(new String(Base64.getDecoder
    .decode(runModeConfiguration.getString(key).getOrElse("")), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = runModeConfiguration.getBoolean(Keys.whitelistEnabled).getOrElse(true)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(Keys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(Keys.whitelistExcludedPaths).map(path => Call("GET", path))
  override lazy val shutterPage: String = getString(Keys.whitelistShutterPage)

  private lazy val signInBaseUrl: String = getString(Keys.signInBaseUrl)
  override lazy val signInContinueBaseUrl: String = runModeConfiguration.getString(Keys.signInContinueBaseUrl).getOrElse("")
  private lazy val signInContinueUrl: String = ContinueUrl(signInContinueBaseUrl + controllers.routes.VatDetailsController.details().url).encodedUrl
  private lazy val signInOrigin = getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override val features = new Features(runModeConfiguration)

  override lazy val vatApiBaseUrl: String = baseUrl("vat-api")
  override lazy val financialDataBaseUrl: String = baseUrl("financial-transactions")

  override lazy val vatSummaryPartial: String = baseUrl("selfLookup") + "/vat-summary-partials/bta-home"

  private lazy val vatReturnsBaseUrl: String = getString(Keys.vatReturnsBase)
  override lazy val vatSubmittedReturnsUrl: String = vatReturnsBaseUrl + getString(Keys.vatSubmittedReturns)
  override lazy val vatReturnDeadlinesUrl: String = vatReturnsBaseUrl + getString(Keys.vatReturnDeadlines)
  override def vatReturnUrl(periodKey: String): String =
    vatReturnsBaseUrl + getString(Keys.vatReturn) + URLEncoder.encode(periodKey, "UTF-8")

  private lazy val btaBaseUrl: String = getString(Keys.businessTaxAccountBase)
  override lazy val btaHomeUrl: String = btaBaseUrl + getString(Keys.businessTaxAccountUrl)

  private lazy val paymentsBaseUrl: String = getString(Keys.paymentsServiceBase)
  private lazy val paymentsUrl: String = getString(Keys.paymentsServiceUrl)
  override lazy val paymentsServiceUrl: String = paymentsBaseUrl + paymentsUrl
  private lazy val paymentsReturnBase: String = getString(Keys.paymentsReturnBase)
  override lazy val paymentsReturnUrl: String = paymentsReturnBase + getString(Keys.paymentsReturnUrl)

  private lazy val btaVatOverviewUrlBase: String = getString(Keys.btaVatOverviewUrlBase)
  override lazy val btaVatOverviewUrl: String = btaVatOverviewUrlBase + getString(Keys.btaVatOverviewUrl)
}
