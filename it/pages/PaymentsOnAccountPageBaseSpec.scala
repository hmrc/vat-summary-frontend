/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package pages

import config.AppConfig
import config.FrontendAppConfig
import helpers.IntegrationBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.{ServiceInfoStub, AuthStub, PaymentsOnAccountStub, CustomerInfoStub}
import stubs.CustomerInfoStub.customerInfoJson
import stubs.ServiceInfoStub
import java.time.LocalDate
import play.api.libs.json.JsValue
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import play.api.Configuration
import play.api.libs.ws.{WSClient}

trait PaymentsOnAccountPageBaseSpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val customerInfo = customerInfoJson(isPartialMigration = false, hasVerifiedEmail = true)

  def setupRequest(responseJson: JsValue, status: Int = Status.OK): WSRequest = {
    AuthStub.authorised()
    CustomerInfoStub.stubCustomerInfo(customerInfo)
    ServiceInfoStub.stubServiceInfoPartial
    PaymentsOnAccountStub.stubStandingRequests(responseJson, status)
    buildRequest(s"/payments-on-account")
  }

  val nextPaymentSelector = "p.govuk-body:nth-of-type(1)"
  val vatPeriodSelector = "div.govuk-summary-card__title-wrapper h2"
  val balanceMessageSelector = "#next-payment-text"
}