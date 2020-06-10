/*
 * Copyright 2020 HM Revenue & Customs
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

  val whitelistEnabled: String = "whitelist.enabled"
  val whitelistedIps: String = "whitelist.allowedIps"
  val whitelistExcludedPaths: String = "whitelist.excludedPaths"
  val whitelistShutterPage: String = "whitelist.shutter-page-url"

  val signInBaseUrl: String = "signIn.url"
  val signInContinueBaseUrl: String = "signIn.continueBaseUrl"

  val userResearchBannerFeature: String = "features.userResearchBanner.enabled"
  val vatCertNSTPsFeature: String = "features.vatCertNSTPs.enabled"
  val staticDateEnabledFeature: String = "features.staticDate.enabled"
  val staticDateValue: String = "date-service.staticDate.value"
  val paymentsAndRepaymentsFeature: String = "features.paymentsAndRepayments.enabled"
  val vatOptOutServiceFeature: String = "features.vatOptOutService.enabled"
  val useVatObligationsService: String = "features.useVatObligationsService.enabled"
  val useDirectDebitDummyPageFeature: String = "features.useDirectDebitDummyPage.enabled"
  val useLanguageSelectorFeature: String = "features.useLanguageSelector.enabled"
  val submitReturnFeatures: String = "features.submitReturnFeatures.enabled"
  val agentAccessFeature: String = "features.agentAccess.enabled"
  val mtdSignUpFeature: String = "features.mtdSignUp.enabled"
  val ddCollectionInProgressFeature: String = "features.ddCollectionInProgress.enabled"
  val displayCovidMessageFeature: String = "features.displayCovid.enabled"
  val missingTraderAddressIntercept: String = "features.missingTraderAddressIntercept.enabled"

  val businessTaxAccountBase: String = "business-tax-account"
  val businessTaxAccountHost: String = "business-tax-account.host"
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
  val mtdVatReSignUpUrl: String = "vat-sign-up-frontend.reSignUpUrl"
  val mtdVatClaimSubscriptionUrl: String = "vat-sign-up-frontend.claimSubscriptionUrl"

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

  val vatRepaymentTrackerFrontendHost: String = "vat-repayment-tracker-frontend.host"
  val vatRepaymentTrackerFrontendUrl: String = "vat-repayment-tracker-frontend.url"

  val vatOptOutFrontendHost: String = "vat-opt-out-frontend.host"
  val vatOptOutFrontendStartUrl: String = "vat-opt-out-frontend.startUrl"

  val deregisterVatHost: String = "deregister-vat-frontend.host"
  val deregisterVatUrl: String = "deregister-vat-frontend.url"

  val govUkSetupAgentServices: String = "gov-uk.setupAgentServicesUrl"
  val govUkAccessibilityUrl: String = "gov-uk.accessibilityUrl"
  val govUkHMRCUrl: String = "gov-uk.hmrcUrl"
  val govUkHearingImpairedUrl: String = "gov-uk.hearingImpairedUrl"
  val govUkVatRegistrationUrl: String = "gov-uk.vatRegistrationUrl"
  val govUkVat7Form: String = "gov-uk.vat7Form"

  val abilityNetUrl: String = "accessibility.abilityNetUrl"
  val wcagGuidelinesUrl: String = "accessibility.wcagGuidelinesUrl"
  val eassUrl: String = "accessibility.eassUrl"
  val ecniUrl: String = "accessibility.ecniUrl"
  val dacUrl: String = "accessibility.dacUrl"

  val manageVatHost: String = "manage-vat-subscription-frontend.host"
  val manageVatUrl: String = "manage-vat-subscription-frontend.url"
  val missingTraderRedirectUrl = "manage-vat-subscription-frontend.missingTraderUrl"
}
