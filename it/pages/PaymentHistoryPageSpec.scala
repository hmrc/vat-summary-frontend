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

package pages

import helpers.IntegrationBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import stubs._
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.CustomerInfoStub.customerInfoJson

class PaymentHistoryPageSpec extends IntegrationBaseSpec {

  "Calling the payment history route as an authenticated user" when {

    val hmrcPaidYouSelector = "#past-payments-2018 > table > tbody > tr:nth-child(1) > td:nth-child(4)"
    val youPaidHmrcSelector = "#past-payments-2018 > table > tbody > tr:nth-child(1) > td:nth-child(3)"
    val noPaymentsInset = "#past-payments-2017 > p"

    def setupRequest(isPartialMigration: Boolean = false): WSRequest = {
      AuthStub.authorised()
      CustomerInfoStub.stubCustomerInfo(customerInfoJson(isPartialMigration = isPartialMigration, hasVerifiedEmail = true))
      ServiceInfoStub.stubServiceInfoPartial
      buildRequest("/payment-history")
    }

    "the user has payments" should {

      "load the page and render the appropriate content" in {

        val request = {
          FinancialDataStub.stubPaidTransactions
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title shouldBe "Payment history - Manage your VAT account - GOV.UK"
        document.select(hmrcPaidYouSelector).text() shouldBe "£600.00"
        document.select(youPaidHmrcSelector).text() shouldBe "£0.00"
      }
    }

    "the user has no payments" should {

      "load the page and render the appropriate content" in {

        val request = {
          FinancialDataStub.stubNoPayments
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title shouldBe "Payment history - Manage your VAT account - GOV.UK"
        document.select(noPaymentsInset).text() shouldBe "You have not made or received any payments using the new VAT service this year."
      }
    }

    "the user sees the previous payments tab" should {

      "load the page and render the appropriate content" in {

        val request = {
          FinancialDataStub.stubPaidTransactions
          AuthStub.authorisedMultipleEnrolments()
          CustomerInfoStub.stubCustomerInfo(customerInfoJson(isPartialMigration = false, hasVerifiedEmail = true))
          ServiceInfoStub.stubServiceInfoPartial
          buildRequest("/payment-history")
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title shouldBe "Payment history - Manage your VAT account - GOV.UK"
        document.select("#previous-payment").text shouldBe
          "You can view your previous payments (opens in a new tab) if you made payments before joining Making Tax Digital."
      }
    }

    "the user is hybrid / partial migration" should {

      "be redirected away" in {

        val request = {
          FinancialDataStub.stubPaidTransactions
          setupRequest(isPartialMigration = true)
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/vat-through-software/vat-overview"
      }
    }
  }
}
