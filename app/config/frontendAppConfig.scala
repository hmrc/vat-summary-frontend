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

package config

import java.net.URLEncoder
import java.util.Base64
import config.features.Features
import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val appName: String
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
  val vatObligationsBaseUrl: String
  val vatSubscriptionBaseUrl: String
  val financialDataBaseUrl: String
  val selfLookup: String
  val viewVatPartial: String
  val claimEnrolmentPartial: String
  val partialMigrationPartial: String
  val vatSubmittedReturnsUrl: String
  val vatReturnDeadlinesUrl: String
  def vatReturnUrl(periodKey: String): String
  val btaBaseUrl: String
  val btaHomeUrl: String
  val btaMessagesUrl: String
  val btaManageAccountUrl: String
  val btaHelpAndContactUrl: String
  val paymentsServiceUrl: String
  val setupPaymentsJourneyPath: String
  val paymentsReturnUrl: String
  val paymentsBackUrl: String
  val unauthenticatedPaymentsUrl: String
  val directDebitReturnUrl: String
  val directDebitBackUrl: String
  val btaVatOverviewUrl: String
  val feedbackFormPartialUrl: String
  val contactFormServiceIdentifier: String
  val staticDateValue: String
  def surveyUrl(identifier: String): String
  def signOutUrl(identifier: String): String
  val mtdVatSignUpUrl: String
  val mtdVatReSignUpUrl: String
  val mtdVatClaimSubscriptionUrl: String
  val unauthorisedSignOutUrl: String
  val timeoutPeriod: Int
  val timeoutCountdown: Int
  val directDebitServiceUrl: String
  val setupDirectDebitsJourneyPath: String
  val directDebitRedirectUrl: String
  val portalMakePaymentUrl: String => String
  val portalPaymentHistoryUrl: String => String
  val portalNonHybridPreviousPaymentsUrl: String => String
  def languageMap:Map[String,Lang]
  val routeToSwitchLanguage: String => Call
  def feedbackUrl(redirect: String): String
  val agentClientLookupStartUrl: String => String
  val agentClientUnauthorisedUrl: String => String
  val agentClientLookupActionUrl: String
  val agentServicesGovUkGuidance: String
  val govUkAccessibilityUrl: String
  val govUkHMRCUrl: String
  val govUkHearingImpairedUrl: String
  val abilityNetUrl: String
  val wcagGuidelinesUrl: String
  val eassUrl: String
  val ecniUrl: String
  val dacUrl: String
  val optOutFrontendUrl: String
  val paymentsAndRepaymentsUrl: String
  val manageVatUrl: String
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

  override lazy val selfLookup: String = baseUrl("selfLookup")

  override lazy val viewVatPartial: String = selfLookup + "/vat-summary-partials/bta-home"
  override lazy val claimEnrolmentPartial: String = selfLookup + "/vat-summary-partials/claim-enrolment"
  override lazy val partialMigrationPartial: String = selfLookup + "/vat-summary-partials/partial-migration"

  private lazy val vatReturnsBaseUrl: String = getString(Keys.vatReturnsBase)

  override lazy val vatObligationsBaseUrl: String = baseUrl(Keys.vatObligations)

  override lazy val vatSubmittedReturnsUrl: String = vatReturnsBaseUrl + getString(Keys.vatSubmittedReturns)
  override lazy val vatReturnDeadlinesUrl: String = vatReturnsBaseUrl + getString(Keys.vatReturnDeadlines)
  override def vatReturnUrl(periodKey: String): String = vatReturnsBaseUrl + getString(Keys.vatReturn) + URLEncoder.encode(periodKey, "UTF-8")

  private lazy val helpAndContactFrontendUrl: String = getString(Keys.helpAndContactFrontendBase)

  override lazy val btaBaseUrl: String = baseUrl(Keys.businessTaxAccountBase)
  override lazy val btaHomeUrl: String = getString(Keys.businessTaxAccountHost) + getString(Keys.businessTaxAccountUrl)
  override lazy val btaMessagesUrl: String = btaHomeUrl + getString(Keys.businessTaxAccountMessagesUrl)
  override lazy val btaManageAccountUrl: String = btaHomeUrl + getString(Keys.businessTaxAccountManageAccountUrl)
  override lazy val btaHelpAndContactUrl: String = helpAndContactFrontendUrl + getString(Keys.helpAndContactHelpUrl)

  override lazy val paymentsServiceUrl: String = baseUrl(Keys.paymentsServiceBase)
  override lazy val setupPaymentsJourneyPath: String = getString(Keys.setupPaymentsJourneyPath)

  override lazy val directDebitServiceUrl: String = baseUrl(Keys.directDebitServiceBase)
  override lazy val setupDirectDebitsJourneyPath: String = getString(Keys.setupDirectDebitJourneyPath)
  override lazy val directDebitRedirectUrl: String = getString(Keys.directDebitRedirectUrl)

  private lazy val paymentsReturnBase: String = getString(Keys.paymentsReturnBase)
  override lazy val paymentsReturnUrl: String = paymentsReturnBase + getString(Keys.paymentsReturnUrl)
  override lazy val paymentsBackUrl: String = paymentsReturnBase + getString(Keys.paymentsBackUrl)

  private lazy val unauthenticatedPaymentsBase: String = getString(Keys.unauthenticatedPaymentsBase)
  override lazy val unauthenticatedPaymentsUrl: String = unauthenticatedPaymentsBase + getString(Keys.unauthenticatedPaymentsUrl)

  private lazy val directDebitReturnBase: String = getString(Keys.directDebitReturnBase)
  override lazy val directDebitReturnUrl: String = directDebitReturnBase + getString(Keys.directDebitReturnUrl)
  override lazy val directDebitBackUrl: String = directDebitReturnBase + getString(Keys.directDebitBackUrl)

  private lazy val btaVatOverviewUrlBase: String = getString(Keys.btaVatOverviewUrlBase)
  override lazy val btaVatOverviewUrl: String = btaVatOverviewUrlBase + getString(Keys.btaVatOverviewUrl)

  override lazy val staticDateValue: String = getString(Keys.staticDateValue)

  private lazy val surveyBaseUrl = getString(Keys.surveyHost) + getString(Keys.surveyUrl)
  override def surveyUrl(identifier: String): String = s"$surveyBaseUrl/$identifier"

  private lazy val governmentGatewayHost: String = getString(Keys.governmentGatewayHost)

  override def signOutUrl(identifier: String): String =
    s"$governmentGatewayHost/gg/sign-out?continue=${surveyUrl(identifier)}"
  override lazy val unauthorisedSignOutUrl: String = s"$governmentGatewayHost/gg/sign-out?continue=$signInContinueUrl"

  private val mtdVatSignUpBaseUrl: String = getString(Keys.mtdVatSignUpBaseUrl)
  override lazy val mtdVatSignUpUrl: String = mtdVatSignUpBaseUrl + getString(Keys.mtdVatSignUpUrl)
  override lazy val mtdVatReSignUpUrl: String = mtdVatSignUpBaseUrl + getString(Keys.mtdVatReSignUpUrl)
  override lazy val mtdVatClaimSubscriptionUrl: String = mtdVatSignUpBaseUrl + getString(Keys.mtdVatClaimSubscriptionUrl)

  override lazy val timeoutPeriod: Int = getString(Keys.timeoutPeriod).toInt
  override lazy val timeoutCountdown: Int = getString(Keys.timeoutCountDown).toInt

  override val portalMakePaymentUrl: String => String = (vrn: String) => {
    s"${getString(Keys.portalPrefix)}/$vrn${getString(Keys.portalMakePaymentPostfix)}"
  }

  override val portalPaymentHistoryUrl: String => String = (vrn: String) => {
    s"${getString(Keys.portalPrefix)}/$vrn${getString(Keys.portalPaymentHistoryPostfix)}"
  }

  override val portalNonHybridPreviousPaymentsUrl: String => String = (vrn: String) => {
    s"${getString(Keys.portalPrefix)}/$vrn${getString(Keys.nonHybridPreviousPaymentsUrl)}"
  }

  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)

  private val host: String = getString(Keys.host)

  override def feedbackUrl(redirect: String): String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${ContinueUrl(host + redirect).encodedUrl}"

  // Agent Client Lookup
  private lazy val platformHost = getString(Keys.host)
  private lazy val agentClientLookupRedirectUrl: String => String = uri => ContinueUrl(platformHost + uri).encodedUrl
  private lazy val agentClientLookupHost = getString(Keys.vatAgentClientLookupFrontendHost)
  override lazy val agentClientLookupStartUrl: String => String = uri =>
    agentClientLookupHost +
      getString(Keys.vatAgentClientLookupFrontendStartUrl) +
      s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
  override lazy val agentClientUnauthorisedUrl: String => String = uri =>
    agentClientLookupHost +
      getString(Keys.vatAgentClientLookupFrontendUnauthorisedUrl) +
      s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
  override lazy val agentClientLookupActionUrl: String = agentClientLookupHost + getString(Keys.vatAgentClientLookupFrontendActionUrl)

  override lazy val agentServicesGovUkGuidance: String = getString(Keys.govUkSetupAgentServices)
  override lazy val govUkAccessibilityUrl: String = getString(Keys.govUkAccessibilityUrl)
  override lazy val govUkHMRCUrl: String = getString(Keys.govUkHMRCUrl)
  override lazy val govUkHearingImpairedUrl: String = getString(Keys.govUkHearingImpairedUrl)

  override lazy val abilityNetUrl: String = getString(Keys.abilityNetUrl)
  override lazy val wcagGuidelinesUrl: String = getString(Keys.wcagGuidelinesUrl)
  override lazy val eassUrl: String = getString(Keys.eassUrl)
  override lazy val ecniUrl: String = getString(Keys.ecniUrl)
  override lazy val dacUrl: String = getString(Keys.dacUrl)

  override lazy val optOutFrontendUrl: String = getString(Keys.vatOptOutFrontendHost) + getString(Keys.vatOptOutFrontendStartUrl)

  override lazy val paymentsAndRepaymentsUrl: String = getString(Keys.vatRepaymentTrackerFrontendHost) + getString(Keys.vatRepaymentTrackerFrontendUrl)

  override lazy val manageVatUrl: String = getString(Keys.manageVatHost) + getString(Keys.manageVatUrl)
}
