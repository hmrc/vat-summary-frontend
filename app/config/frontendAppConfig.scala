/*
 * Copyright 2021 HM Revenue & Customs
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

import config.features.Features
import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait AppConfig {
  val appName: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
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
  val payViewAndChange: String
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
  val agentClientLookupHubUrl: String
  val agentServicesGovUkGuidance: String
  val govUkAccessibilityUrl: String
  val govUkHMRCUrl: String
  val govUkHearingImpairedUrl: String
  val govUkVatRegistrationUrl: String
  val govUkVat7Form: String
  val abilityNetUrl: String
  val wcagGuidelinesUrl: String
  val eassUrl: String
  val ecniUrl: String
  val dacUrl: String
  val optOutFrontendUrl: String
  val deregisterVatUrl: String
  val paymentsAndRepaymentsUrl: String
  val manageVatUrl: String
  val reportA11yProblemUrl: String
  val a11yServiceIdentifier: String
  val missingTraderRedirectUrl: String
  val verifyEmailUrl: String
  val gtmContainer: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, sc: ServicesConfig) extends AppConfig {

  override val appName: String = sc.getString("appName")
  private lazy val contactHost: String = sc.getString(Keys.contactFrontendHost)
  override lazy val contactFormServiceIdentifier: String = "VATVC"
  private lazy val contactFrontendService = sc.baseUrl(Keys.contactFrontendService)
  override lazy val a11yServiceIdentifier: String = "VATVCACCESSIBILITY"
  override lazy val reportA11yProblemUrl = s"$contactHost/contact/accessibility?service=$a11yServiceIdentifier"
  override lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  override lazy val feedbackFormPartialUrl: String = s"$contactFrontendService/contact/beta-feedback/form"

  override lazy val authUrl: String = sc.baseUrl("auth")

  private lazy val signInBaseUrl: String = sc.getString(Keys.signInBaseUrl)
  override lazy val signInContinueBaseUrl: String = runModeConfiguration.getOptional[String](Keys.signInContinueBaseUrl).getOrElse("")
  private lazy val signInContinueUrl: String = SafeRedirectUrl(signInContinueBaseUrl + controllers.routes.VatDetailsController.details().url).encodedUrl
  private lazy val signInOrigin = sc.getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  override val features = new Features()(runModeConfiguration)

  override lazy val vatSubscriptionBaseUrl: String = sc.baseUrl("vat-subscription")
  override lazy val vatApiBaseUrl: String = sc.baseUrl("vat-api")
  override lazy val financialDataBaseUrl: String = sc.baseUrl("financial-transactions")

  override lazy val selfLookup: String = sc.baseUrl("selfLookup")

  override lazy val viewVatPartial: String = selfLookup + "/vat-summary-partials/bta-home"
  override lazy val claimEnrolmentPartial: String = selfLookup + "/vat-summary-partials/claim-enrolment"
  override lazy val partialMigrationPartial: String = selfLookup + "/vat-summary-partials/partial-migration"

  private lazy val vatReturnsBaseUrl: String = sc.getString(Keys.vatReturnsBase)

  override lazy val vatObligationsBaseUrl: String = sc.baseUrl(Keys.vatObligations)

  override lazy val vatSubmittedReturnsUrl: String = vatReturnsBaseUrl + sc.getString(Keys.vatSubmittedReturns)
  override lazy val vatReturnDeadlinesUrl: String = vatReturnsBaseUrl + sc.getString(Keys.vatReturnDeadlines)
  override def vatReturnUrl(periodKey: String): String = vatReturnsBaseUrl + sc.getString(Keys.vatReturn) + URLEncoder.encode(periodKey, "UTF-8")

  private lazy val helpAndContactFrontendUrl: String = sc.getString(Keys.helpAndContactFrontendBase)

  override lazy val btaBaseUrl: String = sc.baseUrl(Keys.businessTaxAccountBase)
  override lazy val btaHomeUrl: String = sc.getString(Keys.businessTaxAccountHost) + sc.getString(Keys.businessTaxAccountUrl)
  override lazy val btaMessagesUrl: String = btaHomeUrl + sc.getString(Keys.businessTaxAccountMessagesUrl)
  override lazy val btaManageAccountUrl: String = btaHomeUrl + sc.getString(Keys.businessTaxAccountManageAccountUrl)
  override lazy val btaHelpAndContactUrl: String = helpAndContactFrontendUrl + sc.getString(Keys.helpAndContactHelpUrl)

  override lazy val paymentsServiceUrl: String = sc.baseUrl(Keys.paymentsServiceBase)
  override lazy val payViewAndChange: String = sc.getString(Keys.payViewAndChangePath)

  override lazy val directDebitServiceUrl: String = sc.baseUrl(Keys.directDebitServiceBase)
  override lazy val setupDirectDebitsJourneyPath: String = sc.getString(Keys.setupDirectDebitJourneyPath)
  override lazy val directDebitRedirectUrl: String = sc.getString(Keys.directDebitRedirectUrl)

  private lazy val paymentsReturnBase: String = sc.getString(Keys.paymentsReturnBase)
  override lazy val paymentsReturnUrl: String = paymentsReturnBase + sc.getString(Keys.paymentsReturnUrl)
  override lazy val paymentsBackUrl: String = paymentsReturnBase + sc.getString(Keys.paymentsBackUrl)

  private lazy val unauthenticatedPaymentsBase: String = sc.getString(Keys.unauthenticatedPaymentsBase)
  override lazy val unauthenticatedPaymentsUrl: String = unauthenticatedPaymentsBase + sc.getString(Keys.unauthenticatedPaymentsUrl)

  override lazy val directDebitReturnUrl: String = host + sc.getString(Keys.directDebitReturnUrl)
  override lazy val directDebitBackUrl: String = host + sc.getString(Keys.directDebitBackUrl)

  private lazy val btaVatOverviewUrlBase: String = sc.getString(Keys.btaVatOverviewUrlBase)
  override lazy val btaVatOverviewUrl: String = btaVatOverviewUrlBase + sc.getString(Keys.btaVatOverviewUrl)

  override lazy val staticDateValue: String = sc.getString(Keys.staticDateValue)

  private lazy val surveyBaseUrl = sc.getString(Keys.surveyHost) + sc.getString(Keys.surveyUrl)
  override def surveyUrl(identifier: String): String = s"$surveyBaseUrl/$identifier"

  private lazy val governmentGatewayHost: String = sc.getString(Keys.governmentGatewayHost)

  override def signOutUrl(identifier: String): String =
    s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=${surveyUrl(identifier)}"
  override lazy val unauthorisedSignOutUrl: String =
    s"$governmentGatewayHost/bas-gateway/sign-out-without-state?continue=$signInContinueUrl"

  private val mtdVatSignUpBaseUrl: String = sc.getString(Keys.mtdVatSignUpBaseUrl)
  override lazy val mtdVatReSignUpUrl: String = mtdVatSignUpBaseUrl + sc.getString(Keys.mtdVatReSignUpUrl)
  override lazy val mtdVatClaimSubscriptionUrl: String = mtdVatSignUpBaseUrl + sc.getString(Keys.mtdVatClaimSubscriptionUrl)

  override lazy val timeoutPeriod: Int = sc.getString(Keys.timeoutPeriod).toInt
  override lazy val timeoutCountdown: Int = sc.getString(Keys.timeoutCountDown).toInt

  override val portalMakePaymentUrl: String => String = (vrn: String) => {
    s"${sc.getString(Keys.portalPrefix)}/$vrn${sc.getString(Keys.portalMakePaymentPostfix)}"
  }

  override val portalPaymentHistoryUrl: String => String = (vrn: String) => {
    s"${sc.getString(Keys.portalPrefix)}/$vrn${sc.getString(Keys.portalPaymentHistoryPostfix)}"
  }

  override val portalNonHybridPreviousPaymentsUrl: String => String = (vrn: String) => {
    s"${sc.getString(Keys.portalPrefix)}/$vrn${sc.getString(Keys.nonHybridPreviousPaymentsUrl)}"
  }

  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)

  private val host: String = sc.getString(Keys.host)

  override def feedbackUrl(redirect: String): String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier" +
    s"&backUrl=${SafeRedirectUrl(host + redirect).encodedUrl}"

  // Agent Client Lookup
  private lazy val platformHost = sc.getString(Keys.host)
  private lazy val agentClientLookupRedirectUrl: String => String = uri => SafeRedirectUrl(platformHost + uri).encodedUrl
  private lazy val agentClientLookupHost = sc.getString(Keys.vatAgentClientLookupFrontendHost)
  override lazy val agentClientLookupStartUrl: String => String = uri =>
    agentClientLookupHost +
      sc.getString(Keys.vatAgentClientLookupFrontendStartUrl) +
      s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
  override lazy val agentClientUnauthorisedUrl: String => String = uri =>
    agentClientLookupHost +
      sc.getString(Keys.vatAgentClientLookupFrontendUnauthorisedUrl) +
      s"?redirectUrl=${agentClientLookupRedirectUrl(uri)}"
  override lazy val agentClientLookupHubUrl: String = agentClientLookupHost + sc.getString(Keys.vatAgentClientLookupFrontendHubUrl)

  override lazy val agentServicesGovUkGuidance: String = sc.getString(Keys.govUkSetupAgentServices)
  override lazy val govUkAccessibilityUrl: String = sc.getString(Keys.govUkAccessibilityUrl)
  override lazy val govUkHMRCUrl: String = sc.getString(Keys.govUkHMRCUrl)
  override lazy val govUkHearingImpairedUrl: String = sc.getString(Keys.govUkHearingImpairedUrl)
  override lazy val govUkVatRegistrationUrl: String = sc.getString(Keys.govUkVatRegistrationUrl)
  override lazy val govUkVat7Form: String = sc.getString(Keys.govUkVat7Form)

  override lazy val abilityNetUrl: String = sc.getString(Keys.abilityNetUrl)
  override lazy val wcagGuidelinesUrl: String = sc.getString(Keys.wcagGuidelinesUrl)
  override lazy val eassUrl: String = sc.getString(Keys.eassUrl)
  override lazy val ecniUrl: String = sc.getString(Keys.ecniUrl)
  override lazy val dacUrl: String = sc.getString(Keys.dacUrl)

  override lazy val optOutFrontendUrl: String = sc.getString(Keys.vatOptOutFrontendHost) + sc.getString(Keys.vatOptOutFrontendStartUrl)

  override lazy val deregisterVatUrl: String = sc.getString(Keys.deregisterVatHost) + sc.getString(Keys.deregisterVatUrl)

  override lazy val paymentsAndRepaymentsUrl: String = sc.getString(Keys.vatRepaymentTrackerFrontendHost) + sc.getString(Keys.vatRepaymentTrackerFrontendUrl)

  override lazy val manageVatUrl: String = sc.getString(Keys.manageVatHost) + sc.getString(Keys.manageVatUrl)
  override lazy val missingTraderRedirectUrl: String = sc.getString(Keys.manageVatHost) + sc.getString(Keys.missingTraderRedirectUrl)

  private val vatCorrespondenceUrl = sc.getString(Keys.vatCorrespondenceHost) + sc.getString(Keys.vatCorrespondenceContext)
  override val verifyEmailUrl: String = vatCorrespondenceUrl + sc.getString(Keys.verifyEmailEndPoint)

  override val gtmContainer: String = sc.getString(Keys.gtmContainer)
}
