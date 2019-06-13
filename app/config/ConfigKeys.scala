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

object ConfigKeys {

  val contactFrontendHost: String = "contact-frontend.host"
  val contactFrontendService: String = "contact-frontend"

  private val googleAnalyticsRoot: String = "google-analytics"
  val googleAnalyticsToken: String = googleAnalyticsRoot + ".token"
  val googleAnalyticsHost: String = googleAnalyticsRoot + ".host"

  val whitelistEnabled: String = "whitelist.enabled"
  val whitelistedIps: String = "whitelist.allowedIps"
  val whitelistExcludedPaths: String = "whitelist.excludedPaths"
  val whitelistShutterPage: String = "whitelist.shutter-page-url"

  val signInBaseUrl: String = "signIn.url"
  val signInContinueBaseUrl: String = "signIn.continueBaseUrl"

  val userResearchBannerFeature: String = "features.userResearchBanner.enabled"
  val allowDirectDebitsFeature: String = "features.allowDirectDebits.enabled"
  val staticDateEnabledFeature: String = "features.staticDate.enabled"
  val staticDateValue: String = "date-service.staticDate.value"
  val vatCertificateFeature: String = "features.vatCertificate.enabled"
  val useVatObligationsService: String = "features.useVatObligationsService.enabled"
  val useDirectDebitDummyPageFeature: String = "features.useDirectDebitDummyPage.enabled"
  val useLanguageSelectorFeature: String = "features.useLanguageSelector.enabled"
  val submitReturnFeatures: String = "features.submitReturnFeatures.enabled"
  val agentAccessFeature: String = "features.agentAccess.enabled"

  val businessTaxAccountBase: String = "business-tax-account.host"
  val businessTaxAccountUrl: String = "business-tax-account.homeUrl"
  val businessTaxAccountMessagesUrl: String = "business-tax-account.messagesUrl"
  val businessTaxAccountManageAccountUrl: String = "business-tax-account.manageAccountUrl"

  val helpAndContactFrontendBase: String = "help-and-contact-frontend.host"
  val helpAndContactHelpUrl: String = "help-and-contact-frontend.helpUrl"

  val btaVatOverviewUrlBase: String = "business-tax-account.vatSummaryHost"
  val btaVatOverviewUrl: String = "business-tax-account.vatSummaryUrl"

  val vatObligations: String = "vat-obligations"

  val vatReturnsBase: String = "view-vat-returns-frontend.host"
  val vatReturnDeadlines: String = "view-vat-returns-frontend.returnDeadlinesUrl"
  val vatSubmittedReturns: String = "view-vat-returns-frontend.submittedReturnsUrl"
  val vatReturn: String = "view-vat-returns-frontend.returnUrl"

  val paymentsServiceBase: String = "pay-api"
  val setupPaymentsJourneyPath: String = "microservice.services.pay-api.endpoints.setupJourney"

  val paymentsReturnBase: String = "payments-frontend.returnHost"
  val paymentsReturnUrl: String = "payments-frontend.returnUrl"
  val paymentsBackUrl: String = "payments-frontend.backUrl"

  val directDebitServiceBase: String = "direct-debit"
  val setupDirectDebitJourneyPath: String = "microservice.services.direct-debit.endpoints.setupJourney"

  val unauthenticatedPaymentsBase: String = "unauthenticatedPayments.host"
  val unauthenticatedPaymentsUrl: String = "unauthenticatedPayments.url"

  val directDebitReturnBase: String = "direct-debit-frontend.returnHost"
  val directDebitReturnUrl: String = "direct-debit-frontend.returnUrl"
  val directDebitBackUrl: String = "direct-debit-frontend.backUrl"
  val directDebitRedirectUrl: String = "direct-debit-frontend.redirectUrl"

  val governmentGatewayHost: String = "government-gateway.host"

  val surveyHost: String = "feedback-frontend.host"
  val surveyUrl: String = "feedback-frontend.url"

  val mtdVatSignUpBaseUrl: String = "vat-sign-up-frontend.host"
  val mtdVatSignUpUrl: String = "vat-sign-up-frontend.signUpUrl"
  val mtdVatClaimSubcriptionUrl: String = "vat-sign-up-frontend.claimSubscriptionUrl"

  val timeoutPeriod: String = "timeout.period"
  val timeoutCountDown: String = "timeout.countDown"

  val portalPrefix: String = "portal.urlPrefix"
  val portalMakePaymentPostfix: String = "portal.makePaymentUrl"
  val portalPaymentHistoryPostfix: String = "portal.paymentHistoryUrl"
  val nonHybridPreviousPaymentsUrl: String = "portal.nonHybridPreviousPaymentsUrl"

  val host = "host"

  val vatAgentClientLookupFrontendHost: String = "vat-agent-client-lookup-frontend.host"
  val vatAgentClientLookupFrontendStartUrl: String = "vat-agent-client-lookup-frontend.startUrl"
  val vatAgentClientLookupFrontendUnauthorisedUrl: String = "vat-agent-client-lookup-frontend.unauthorisedUrl"
  val vatAgentClientLookupFrontendActionUrl: String = "vat-agent-client-lookup-frontend.agentActionUrl"
  val govUkSetupAgentServices: String = "gov-uk.guidance.setupAgentServices.url"
}
