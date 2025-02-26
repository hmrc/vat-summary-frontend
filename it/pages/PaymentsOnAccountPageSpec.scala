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

class PaymentsOnAccountPageSpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  println("DATE")
  println(LocalDate.now())
    
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

  "Calling the Payments On Account route as an authenticated user" when {

    "when there are upcoming payments" should {

      "load the page and display the correct next payment details" in {
        val request = setupRequest(PaymentsOnAccountStub.futurePOAJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "Payments on account - Manage your VAT account - GOV.UK"
        document.select(nextPaymentSelector).text() should include("Your next payment of £22,945.23 is due on 31 May 2025.")
      }
    }

    "when there is a balancing payment due" should {

      "display the correct balancing payment message" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        println(document)
        document.select(balanceMessageSelector).text() shouldBe "Your next payment due is your balancing payment."
      }
    }

    "there are multiple VAT periods" should {

      "display VAT periods correctly" in {
        val request = setupRequest(PaymentsOnAccountStub.todaysPOAJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.select(vatPeriodSelector).text() shouldBe "VAT period: 1 January 2025 to 31 March 2025"
      }
    }

    "the user has no payments due" should {

      "return an internal server error" in {
        val request = setupRequest(PaymentsOnAccountStub.emptyStandingRequestJson, Status.INTERNAL_SERVER_ERROR)

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "there is an API error" should {

      "return an internal server error" in {
        val request = setupRequest(PaymentsOnAccountStub.errorJson, Status.INTERNAL_SERVER_ERROR)

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}