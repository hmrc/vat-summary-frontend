/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pages

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuthStub, CustomerInfoStub, FinancialDataStub}
import stubs.CustomerInfoStub.customerInfoHybridUser

class PaymentsPageSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping
    def request(): WSRequest = {
      setupStubs()
      buildRequest("/what-you-owe")
    }
  }

  "Calling the payments route with an authenticated user" when {

    "the user has an open payment" should {

      "return 200" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo()
          FinancialDataStub.stubSuccessfulDirectDebit
          FinancialDataStub.stubAllOutstandingOpenPayments
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

      "return an open payment" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo()
          FinancialDataStub.stubSuccessfulDirectDebit
          FinancialDataStub.stubAllOutstandingOpenPayments
        }

        val response: WSResponse = await(request().get())
        lazy implicit val document: Document = Jsoup.parse(response.body)
        val firstPaymentAmount = "#payment-section-1 span:nth-of-type(1)"

        document.select(firstPaymentAmount).text().length > 0 shouldBe true
      }
    }

    "the user has no open payments" should {

      "return 200" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo()
          FinancialDataStub.stubSuccessfulDirectDebit
          FinancialDataStub.stubNoPayments
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

      "return no payments" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo()
          FinancialDataStub.stubNoPayments
          FinancialDataStub.stubSuccessfulDirectDebit
        }

        val response: WSResponse = await(request().get())
        lazy implicit val document: Document = Jsoup.parse(response.body)
        val firstPaymentAmount = "#payment-section-1"

        document.select(firstPaymentAmount) shouldBe empty
      }
    }
    "the user has a partial migration" should {

      "redirect to /vat-overview" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo(customerInfoHybridUser)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/vat-through-software/vat-overview"
      }
    }

  }
}
