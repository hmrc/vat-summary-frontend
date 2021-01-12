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

package mocks

import java.net.URLEncoder

import config.AppConfig
import config.features.Features
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Mode}

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override def feedbackUrl(redirect: String): String = ""
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val authUrl: String = ""
  override val vatApiBaseUrl: String = ""
  override val signInUrl: String = ""
  override val signInContinueBaseUrl: String = ""
  override val features: Features = new Features()(runModeConfiguration)
  override val viewVatPartial: String = ""
  override val claimEnrolmentPartial: String = ""
  override val partialMigrationPartial: String = ""
  override val vatSubmittedReturnsUrl: String = "returns-url"
  override val vatReturnDeadlinesUrl: String = ""
  override def vatReturnUrl(periodKey: String): String = s"/submitted/${URLEncoder.encode(periodKey, "UTF-8")}"
  override val vatObligationsBaseUrl: String = "/obligations-api"
  override val financialDataBaseUrl = ""
  override val btaBaseUrl: String = ""
  override val btaHomeUrl: String = "bta-url"
  override val btaHelpAndContactUrl: String = "bta-help-and-contact-url"
  override val btaManageAccountUrl: String = "bta-manage-account-url"
  override val btaMessagesUrl: String = "bta-messages-url"
  override val payViewAndChange: String = "/view-and-change/vat/"
  override val paymentsServiceUrl: String = "/pay-api"
  override val directDebitServiceUrl: String = "direct-debits-url"
  override val setupDirectDebitsJourneyPath: String = "/direct-debit/start"
  override val paymentsReturnUrl: String = "payments-return-url"
  override val paymentsBackUrl: String = "payments-back-url"
  override val unauthenticatedPaymentsUrl: String = "unauthenticated-payments-url"
  override val directDebitReturnUrl: String = "direct-debit-return-url"
  override val directDebitBackUrl: String = "direct-debit-back-url"
  override val directDebitRedirectUrl: String = "direct-debit-redirect-url"
  override val btaVatOverviewUrl: String = "mock-url"
  override val feedbackFormPartialUrl: String = "BasefeedbackUrl"
  override val contactFormServiceIdentifier: String = "VATVC"
  override val a11yServiceIdentifier: String = "VATVCACCESSIBILITY"
  override val staticDateValue: String = "2018-05-01"
  override def surveyUrl(identifier: String): String = "/some-survey-url"
  override val mtdVatReSignUpUrl: String = "/vat-through-software/sign-up/vat-number/"
  override val mtdVatClaimSubscriptionUrl: String = "mtd-claim-subscription"
  override def signOutUrl(identifier: String): String = "/some-gg-signout-url"
  override val unauthorisedSignOutUrl: String = "/unauth-signout"
  override val vatSubscriptionBaseUrl: String = ""
  override val appName: String = "vat-summary-frontend"
  override val timeoutPeriod: Int = 1800
  override val timeoutCountdown: Int = 20
  override val selfLookup: String = ""
  override val portalMakePaymentUrl: String => String = (vrn: String) => "/whatYouOwePortal"
  override val portalPaymentHistoryUrl: String => String = (vrn: String) => "/paymentHistoryPortal"
  override val portalNonHybridPreviousPaymentsUrl: String => String = (vrn: String) => "/previousPaymentsPortal"
  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val agentClientLookupStartUrl: String => String = uri => s"agent-client-lookup-start-url/$uri"
  override val agentClientUnauthorisedUrl: String => String = uri => s"agent-client-unauthorised-url/$uri"
  override val agentServicesGovUkGuidance: String = "guidance/get-an-hmrc-agent-services-account"
  override val govUkAccessibilityUrl: String = "/a11y"
  override val govUkHMRCUrl: String = "/hmrc"
  override val govUkHearingImpairedUrl: String = "/hearing-impaired"
  override val govUkVatRegistrationUrl: String = "/register-for-vat"
  override val govUkVat7Form: String = "/vat-application-to-cancel-your-vat-registration-vat7"
  override val abilityNetUrl: String = "/ability-net"
  override val wcagGuidelinesUrl: String = "/wcag"
  override val eassUrl: String = "/eass"
  override val ecniUrl: String = "/ecni"
  override val dacUrl: String = "/dac"
  override val agentClientLookupHubUrl: String = "agent-client-lookup-hub-url"
  override val optOutFrontendUrl: String = "/vat-through-software/account/opt-out"
  override val deregisterVatUrl: String = "/vat-through-software/account/deregister"
  override val manageVatUrl: String = "/vat-through-software/account/change-business-details"
  override val paymentsAndRepaymentsUrl: String = "/vat-repayment-tracker/manage-or-track-vrt"
  override val reportA11yProblemUrl: String =
    "/contact/accessibility?service=VATVCACCESSIBILITY&userAction=/vat-through-software/vat-overview"
  override val missingTraderRedirectUrl: String = "/missing-trader"
  override val verifyEmailUrl: String = "/verify-email"
  override val gtmContainer: String = "x"
}
