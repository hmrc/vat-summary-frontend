/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.Configuration

class MockAppConfig(val runModeConfiguration: Configuration) extends AppConfig {
  override val host: String = "http://localhost:1234"
  override def feedbackUrl(redirect: String): String = ""
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val authUrl: String = ""
  override val signInUrl: String = ""
  override val signInContinueBaseUrl: String = ""
  override val features: Features = new Features()(runModeConfiguration)
  override val vatSubmittedReturnsUrl: String = "returns-url"
  override val vatReturnDeadlinesUrl: String = "/return-deadlines"
  override def vatReturnUrl(periodKey: String): String = s"/submitted/${URLEncoder.encode(periodKey, "UTF-8")}"

  override val vatObligationsBaseUrl: String = "/obligations-api"
  override val financialDataBaseUrl = ""
  override val btaBaseUrl: String = ""
  override val btaHomeUrl: String = "bta-url"
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
  override val feedbackFormPartialUrl: String = "BasefeedbackUrl"
  override val contactFormServiceIdentifier: String = "VATVC"
  override val a11yServiceIdentifier: String = "VATVCACCESSIBILITY"
  override val staticDateValue: String = "2018-05-01"
  override def surveyUrl(identifier: String): String = "/some-survey-url"
  override val mtdVatReSignUpUrl: String = "/vat-through-software/sign-up/vat-number/"
  override def signOutUrl(identifier: String): String = "/some-gg-signout-url"
  override val unauthorisedSignOutUrl: String = "/unauth-signout"
  override val vatSubscriptionBaseUrl: String = ""
  override val appName: String = "vat-summary-frontend"
  override val timeoutPeriod: Int = 1800
  override val timeoutCountdown: Int = 20
  override val selfLookup: String = ""
  override val portalMakePaymentUrl: String => String = _ => "/whatYouOwePortal"
  override val portalPaymentHistoryUrl: String => String = _ => "/paymentHistoryPortal"
  override val portalNonHybridPreviousPaymentsUrl: String => String = _ => "/previousPaymentsPortal"
  override val routeToSwitchLanguage: String => Call = (lang: String) => controllers.routes.LanguageController.switchToLanguage(lang)
  override def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )
  override val agentClientLookupStartUrl: String => String = uri => s"agent-client-lookup-start-url/$uri"
  override val agentClientUnauthorisedUrl: String => String = uri => s"agent-client-unauthorised-url/$uri"
  override val agentServicesGovUkGuidance: String = "guidance/get-an-hmrc-agent-services-account"
  override val govUkHMRCUrl: String = "/hmrc"
  override val govUkHearingImpairedUrl: String = "/hearing-impaired"
  override val govUkVatRegistrationUrl: String = "/register-for-vat"
  override val govUkVat7Form: String = "/vat-application-to-cancel-your-vat-registration-vat7"
  override val govUKDifficultiesPayingUrl: String = "/difficulties-paying"
  override val govUkPayVATUrl: String = "/pay-vat"
  override val govUKCorrections: String = "/corrections"
  override val abilityNetUrl: String = "/ability-net"
  override val wcagGuidelinesUrl: String = "/wcag"
  override val eassUrl: String = "/eass"
  override val ecniUrl: String = "/ecni"
  override val dacUrl: String = "/dac"
  override val agentClientLookupHubUrl: String = "agent-client-lookup-hub-url"
  override val deregisterVatUrl: String = "/vat-through-software/account/deregister"
  override val manageVatUrl: String = "/vat-through-software/account/change-business-details"
  override val paymentsAndRepaymentsUrl: String = "/vat-repayment-tracker/manage-or-track-vrt"
  override val reportA11yProblemUrl: String =
    "/contact/accessibility?service=VATVCACCESSIBILITY&userAction=/vat-through-software/vat-overview"
  override val missingTraderRedirectUrl: String = "/missing-trader"
  override val verifyEmailUrl: String = "/verify-email"
  override val fixEmailUrl: String = "/fix-your-email"
  override val gtmContainer: String = "x"
  override val environmentHost: String = "localhost"
  override val penaltiesUrl: String => String = (vrn: String) => s"/vat/penalties/summary/$vrn"
  override val penaltiesFrontendUrl: String = "/vat-through-software/test-only/penalties-stub"
  override val mtdGuidance: String = "/when-to-start-using-making-tax-digital-for-vat-if-youve-not-before"
  override val latePaymentGuidanceUrl: String = "/guidance/late-payment-interest-if-you-do-not-pay-vat-or-penalties-on-time"
  override val penaltiesChangesUrl: String = "/government/collections/vat-penalties-and-interest"
  override val govUkPrevIntRateUrl: String = "/rates-and-allowances-hmrc-interest-rates-for-late-and-early-payments/rates-and-allowances-hmrc-interest-rates"

  override val timeToLiveInSeconds: Int = 100

  override val essttpService: String = "/essttp"
  override val webchatUrl: String = "/ask-hmrc/chat/vat-online?ds"
}