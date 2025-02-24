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
import stubs.ServiceInfoStub
import stubs.AuthStub

class PaymentsOnAccountPageSpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  def setupRequest(): WSRequest = {
    AuthStub.authorised()
    ServiceInfoStub.stubServiceInfoPartial
    buildRequest("/payments-on-account")
  }

  val nextPaymentSelector = "p.govuk-body:nth-of-type(1)"
  val vatPeriodSelector = "div.govuk-summary-card__title-wrapper h2"
  val balanceMessageSelector = "p.govuk-body strong"

  "Calling the Payments On Account route as an authenticated user" when {

    "there are upcoming payments" should {

      "load the page and display the correct next payment details" in {
        val request = {
          PaymentsOnAccountStub.stubPaymentsOnAccount()
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "Payments on account - Manage your VAT account - GOV.UK"
        document.select(nextPaymentSelector).text() should include("Your next payment of £22,945.23 is due on 31 March 2024.")
      }
    }

    "there is a balancing payment due" should {

      "display the correct balancing payment message" in {
        val request = {
          PaymentsOnAccountStub.stubBalancingPayment()
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.select(balanceMessageSelector).text() shouldBe "Your next payment due is your balancing payment."
      }
    }

    "there are multiple VAT periods" should {

      "display all VAT periods correctly" in {
        val request = {
          PaymentsOnAccountStub.stubMultipleVatPeriods()
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.select(vatPeriodSelector).text() should include("1 Feb 2024 to April 2024")
        document.select(vatPeriodSelector).text() should include("1 May 2024 to July 2024")
      }
    }

    "the user has no payments due" should {

      "load the page without payment details" in {
        val request = {
          PaymentsOnAccountStub.stubNoPayments()
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.select(nextPaymentSelector).size() shouldBe 0
      }
    }
  }
}
