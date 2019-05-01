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

package mocks

import java.net.URLEncoder

import config.AppConfig
import config.features.Features
import play.api.Mode.Mode
import play.api.i18n.Lang
import play.api.mvc.Call
import play.api.{Configuration, Mode}

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override def feedbackUrl(redirect: String): String = ""
  override val analyticsToken: String = ""
  override val analyticsHost: String = ""
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val whitelistEnabled: Boolean = false
  override val whitelistedIps: Seq[String] = Seq("")
  override val whitelistExcludedPaths: Seq[Call] = Nil
  override val shutterPage: String = "https://www.tax.service.gov.uk/shutter/vat-through-software"
  override val authUrl: String = ""
  override val vatApiBaseUrl: String = ""
  override val signInUrl: String = ""
  override val signInContinueBaseUrl: String = ""
  override val features: Features = new Features(runModeConfiguration)
  override val viewVatPartial: String = ""
  override val claimEnrolmentPartial: String = ""
  override val partialMigrationPartial: String = ""
  override val vatSubmittedReturnsUrl: String = "returns-url"
  override val vatReturnDeadlinesUrl: String = ""
  override def vatReturnUrl(periodKey: String): String = s"/submitted/${URLEncoder.encode(periodKey, "UTF-8")}"
  override val vatObligationsBaseUrl: String = "/obligations-api"
  override val financialDataBaseUrl = ""
  override val btaHomeUrl: String = "bta-url"
  override val paymentsServiceUrl: String = "payments-url"
  override val setupPaymentsJourneyPath: String = "/payment/start"
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
  override val staticDateValue: String = "2018-05-01"
  override val surveyUrl: String = "/some-survey-url"
  override val mtdVatSignUpUrl: String = "mtd-sign-up"
  override val mtdVatClaimSubscriptionUrl: String = "mtd-claim-subscription"
  override val signOutUrl: String = "/some-gg-signout-url"
  override val unauthorisedSignOutUrl: String = ""
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
}
