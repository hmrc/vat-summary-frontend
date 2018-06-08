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

import config.features.Features
import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val appName: String
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
  val vatSubscriptionBaseUrl: String
  val financialDataBaseUrl: String
  val vatSummaryPartial: String
  val vatSubmittedReturnsUrl: String
  val vatReturnDeadlinesUrl: String
  def vatReturnUrl(periodKey: String): String
  val btaHomeUrl: String
  val paymentsServiceUrl: String
  val setupPaymentsJourneyPath: String
  val paymentsReturnUrl: String
  val paymentsBackUrl: String
  val unauthenticatedPaymentsUrl: String
  val btaVatOverviewUrl: String
  val feedbackFormPartialUrl: String
  val contactFormServiceIdentifier: String
  val staticDateValue: String
  val surveyUrl: String
  val signOutUrl: String
  val mtdVatSignUpUrl: String
  val unauthorisedSignOutUrl: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int
  val directDebitServiceUrl: String
  val setupDirectDebitJourneyPath: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, val environment: Environment) extends AppConfig {

  override val mode: Mode = environment.mode

  override val appName: String = getString("appName")
  private lazy val contactHost: String = getString(Keys.contactFrontendHost)
  override lazy val contactFormServiceIdentifier: String = "VATVC"
  private lazy val contactFrontendService = baseUrl(Keys.contactFrontendService)
  override lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val feedbackFormPartialUrl: String = s"$contactFrontendService/contact/beta-feedback/form"

  override lazy val authUrl: String = baseUrl("auth")
  override lazy val analyticsToken: String = getString(Keys.googleAnalyticsToken)
  override lazy val analyticsHost: String = getString(Keys.googleAnalyticsHost)

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

  override lazy val vatSubscriptionBaseUrl: String = baseUrl("vat-subscription")
  override lazy val vatApiBaseUrl: String = baseUrl("vat-api")
  override lazy val financialDataBaseUrl: String = baseUrl("financial-transactions")

  override lazy val vatSummaryPartial: String = baseUrl("selfLookup") + "/vat-summary-partials/bta-home"

  private lazy val vatReturnsBaseUrl: String = getString(Keys.vatReturnsBase)
  override lazy val vatSubmittedReturnsUrl: String = vatReturnsBaseUrl + getString(Keys.vatSubmittedReturns)
  override lazy val vatReturnDeadlinesUrl: String = vatReturnsBaseUrl + getString(Keys.vatReturnDeadlines)
  override def vatReturnUrl(periodKey: String): String = vatReturnsBaseUrl + getString(Keys.vatReturn) + URLEncoder.encode(periodKey, "UTF-8")

  private lazy val btaBaseUrl: String = getString(Keys.businessTaxAccountBase)
  override lazy val btaHomeUrl: String = btaBaseUrl + getString(Keys.businessTaxAccountUrl)

  override lazy val paymentsServiceUrl: String = baseUrl(Keys.paymentsServiceBase)
  override lazy val setupPaymentsJourneyPath: String = getString(Keys.setupPaymentsJourneyPath)

  override lazy val directDebitServiceUrl: String = baseUrl(Keys.directDebitServiceBase)
  override lazy val setupDirectDebitJourneyPath: String = getString(Keys.setupDirectDebitJourneyPath)

  private lazy val paymentsReturnBase: String = getString(Keys.paymentsReturnBase)
  override lazy val paymentsReturnUrl: String = paymentsReturnBase + getString(Keys.paymentsReturnUrl)
  override lazy val paymentsBackUrl: String = paymentsReturnBase + getString(Keys.paymentsBackUrl)

  private lazy val unauthenticatedPaymentsBase: String = getString(Keys.unauthenticatedPaymentsBase)
  override lazy val unauthenticatedPaymentsUrl: String = unauthenticatedPaymentsBase + getString(Keys.unauthenticatedPaymentsUrl)

  private lazy val btaVatOverviewUrlBase: String = getString(Keys.btaVatOverviewUrlBase)
  override lazy val btaVatOverviewUrl: String = btaVatOverviewUrlBase + getString(Keys.btaVatOverviewUrl)

  override lazy val staticDateValue: String = getString(Keys.staticDateValue)

  private lazy val surveyBaseUrl = getString(Keys.surveyHost) + getString(Keys.surveyUrl)
  override lazy val surveyUrl = s"$surveyBaseUrl/?origin=$contactFormServiceIdentifier"

  private lazy val governmentGatewayHost: String = getString(Keys.governmentGatewayHost)

  override lazy val signOutUrl = s"$governmentGatewayHost/gg/sign-out?continue=$surveyUrl"
  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$signInContinueUrl"

  private val mtdVatSignUpBaseUrl: String = getString(Keys.mtdVatSignUpBaseUrl)
  override lazy val mtdVatSignUpUrl: String = mtdVatSignUpBaseUrl + getString(Keys.mtdVatSignUpUrl)

  override lazy val timeoutPeriod: Int = getString(Keys.timeoutPeriod).toInt
  override lazy val timeoutCountdown: Int = getString(Keys.timeoutCountDown).toInt

}
