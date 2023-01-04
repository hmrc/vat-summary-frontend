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

package config

object ConfigKeys {

  val contactFrontendHost: String = "contact-frontend.host"
  val contactFrontendIdentifier: String = "contact-frontend.serviceId"
  val contactFrontendService: String = "contact-frontend"

  val signInBaseUrl: String = "signIn.url"
  val signInContinueBaseUrl: String = "signIn.continueBaseUrl"

  val userResearchBannerFeature: String = "features.userResearchBanner.enabled"
  val staticDateEnabledFeature: String = "features.staticDate.enabled"
  val staticDateValue: String = "date-service.staticDate.value"
  val penaltiesAndInterestWYOFeature: String = "features.penaltiesAndInterestWYO.enabled"
  val chargeReferenceInsetFeature: String = "features.chargeReferenceInset.enabled"
  val overdueTimeToPayDescriptionFeature: String = "features.overdueTimeToPayDescription.enabled"

  val businessTaxAccountBase: String = "business-tax-account"
  val businessTaxAccountHost: String = "business-tax-account.host"
  val businessTaxAccountUrl: String = "business-tax-account.homeUrl"

  val vatObligations: String = "vat-obligations"

  val vatReturnsBase: String = "view-vat-returns-frontend.host"
  val vatReturnDeadlines: String = "view-vat-returns-frontend.returnDeadlinesUrl"
  val vatSubmittedReturns: String = "view-vat-returns-frontend.submittedReturnsUrl"
  val vatReturn: String = "view-vat-returns-frontend.returnUrl"

  val paymentsServiceBase: String = "pay-api"
  val payViewAndChangePath: String = "microservice.services.pay-api.endpoints.payViewAndChange"

  val paymentsReturnBase: String = "payments-frontend.returnHost"
  val paymentsReturnUrl: String = "payments-frontend.returnUrl"
  val paymentsBackUrl: String = "payments-frontend.backUrl"

  val directDebitServiceBase: String = "direct-debit"
  val setupDirectDebitJourneyPath: String = "microservice.services.direct-debit.endpoints.setupJourney"

  val unauthenticatedPaymentsBase: String = "unauthenticatedPayments.host"
  val unauthenticatedPaymentsUrl: String = "unauthenticatedPayments.url"

  val directDebitReturnUrl: String = "direct-debit-frontend.returnUrl"
  val directDebitBackUrl: String = "direct-debit-frontend.backUrl"
  val directDebitRedirectUrl: String = "direct-debit-frontend.redirectUrl"

  val governmentGatewayHost: String = "government-gateway.host"

  val surveyHost: String = "feedback-frontend.host"
  val surveyUrl: String = "feedback-frontend.url"

  val mtdVatSignUpBaseUrl: String = "vat-sign-up-frontend.host"
  val mtdVatReSignUpUrl: String = "vat-sign-up-frontend.reSignUpUrl"

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
  val vatAgentClientLookupFrontendHubUrl: String = "vat-agent-client-lookup-frontend.agentHubUrl"

  val vatRepaymentTrackerFrontendHost: String = "vat-repayment-tracker-frontend.host"
  val vatRepaymentTrackerFrontendUrl: String = "vat-repayment-tracker-frontend.url"

  val deregisterVatHost: String = "deregister-vat-frontend.host"
  val deregisterVatUrl: String = "deregister-vat-frontend.url"

  val govUkSetupAgentServices: String = "gov-uk.setupAgentServicesUrl"
  val govUkHMRCUrl: String = "gov-uk.hmrcUrl"
  val govUkHearingImpairedUrl: String = "gov-uk.hearingImpairedUrl"
  val govUkVatRegistrationUrl: String = "gov-uk.vatRegistrationUrl"
  val govUkVat7Form: String = "gov-uk.vat7Form"
  val govUkPayVATUrl: String = "gov-uk.payVat"
  val govUKDifficultiesPayingUrl = "gov-uk.difficultiesPaying"
  val govUKCorrections = "gov-uk.vatCorrections"
  val mtdGuidance = "gov-uk.mtdGuidance"
  val govUkHoldingUrl = "gov-uk.govUkHolding"
  val penaltiesChangesUrl = "gov-uk.penaltiesChanges"
  val govUkPrevIntRateUrl = "gov-uk.govUkPrevIntRate"

  val abilityNetUrl: String = "accessibility.abilityNetUrl"
  val wcagGuidelinesUrl: String = "accessibility.wcagGuidelinesUrl"
  val eassUrl: String = "accessibility.eassUrl"
  val ecniUrl: String = "accessibility.ecniUrl"
  val dacUrl: String = "accessibility.dacUrl"

  val manageVatHost: String = "manage-vat-subscription-frontend.host"
  val manageVatUrl: String = "manage-vat-subscription-frontend.url"
  val missingTraderRedirectUrl = "manage-vat-subscription-frontend.missingTraderUrl"

  val vatCorrespondenceHost: String = "vat-correspondence-details-frontend.host"
  val vatCorrespondenceContext: String = "vat-correspondence-details-frontend.context"
  val verifyEmailEndPoint: String = "vat-correspondence-details-frontend.endpoints.verifyEmail"
  val fixEmail: String = "vat-correspondence-details-frontend.fixEmail"

  val gtmContainer: String = "tracking-consent-frontend.gtm.container"

  val environmentHost: String = "environment-base.host"

  val penaltiesServiceEnabledFeature: String = "features.penaltiesService.enabled"
  val penaltiesHost: String = "penalties.host"
  val penaltiesUrl: String = "penalties.url"
  val penaltiesFrontendHost: String = "penalties-frontend.host"
  val penaltiesFrontendUrl: String = "penalties-frontend.endpointUrl"

  val timeToLive: String = "mongodb.timeToLiveInSeconds"
}
