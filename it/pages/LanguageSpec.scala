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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pages

import helpers.IntegrationBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs._

class LanguageSpec extends IntegrationBaseSpec {

  val obligationsStub: VatObligationsStub = new VatObligationsStub(vatObligationsServiceEnabled = true)

  private trait Test {
    def request(): WSRequest = {
      AuthStub.authorised()
      obligationsStub.stubOutstandingObligations
      CustomerInfoStub.stubCustomerInfo()
      CustomerInfoStub.stubCustomerMandationStatus()
      FinancialDataStub.stubOutstandingTransactions
      ServiceInfoStub.stubServiceInfoPartial
      buildRequest("/vat-overview")
    }
  }

  "Calling a generic page route" when {

    "language preference is set in the cookie" when {

      "language is 'en'" should {

        "return the page in English" in new Test {

          val response: WSResponse = await(request().withHeaders("Cookie" -> "PLAY_LANG=en;").get())

          lazy val document: Document = Jsoup.parse(response.body)

          document.title() shouldBe "Your VAT account - Business tax account - GOV.UK"
        }
      }

      "language is 'cy'" should {

        "return the page in Welsh" in new Test {

          val response: WSResponse = await(request().withHeaders("Cookie" -> "PLAY_LANG=cy;").get())

          lazy val document: Document = Jsoup.parse(response.body)

          document.title() shouldBe "Eich cyfrif TAW - Cyfrif Treth Busnes - GOV.UK"
        }
      }
    }

    "language preference is not set in the cookie" should {

      "return the page in English" in new Test {

        val response: WSResponse = await(request().get())

        lazy val document: Document = Jsoup.parse(response.body)

        document.title() shouldBe "Your VAT account - Business tax account - GOV.UK"
      }
    }
  }
}
