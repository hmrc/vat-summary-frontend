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

class PaymentsOnAccountPageSpec extends PaymentsOnAccountPageBaseSpec {

  override def servicesConfig: Map[String, String] = super.servicesConfig ++ Map(
    "date-service.staticDate.value" -> "2025-03-10"
  )

  "Calling the Payments On Account route as an authenticated user" when {

    "when there are upcoming payments" should {

      "load the page and display the correct next payment details" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "Payments on account - Manage your VAT account - GOV.UK"
        document.select(nextPaymentSelector).text() should include("Your next payment of £122,945.23 is due on 31 Mar 2025.")
      }
    }

    "there are multiple VAT periods" should {

      "display VAT periods correctly" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)
        val extractedText = document.select(vatPeriodSelector).text()

        response.status shouldBe Status.OK
        

        extractedText should include("VAT period: 1 February 2025 to 30 April 2025")
        extractedText should include("VAT period: 1 May 2025 to 31 July 2025")
        extractedText should include("VAT period: 1 August 2025 to 31 October 2025")
        extractedText should include("VAT period: 1 November 2025 to 31 January 2026")

        extractedText should include("VAT period: 1 February 2024 to 30 April 2024")
        extractedText should include("VAT period: 1 May 2024 to 31 July 2024")
        extractedText should include("VAT period: 1 August 2024 to 31 October 2024")
        extractedText should include("VAT period: 1 November 2024 to 31 January 2025")
      }


      "display current VAT periods correctly" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)
        val extractedText = document.select("#current-schedule").text()

        response.status shouldBe Status.OK
        

        extractedText should include("VAT period: 1 February 2025 to 30 April 2025")
        extractedText should include("VAT period: 1 May 2025 to 31 July 2025")
        extractedText should include("VAT period: 1 August 2025 to 31 October 2025")
        extractedText should include("VAT period: 1 November 2025 to 31 January 2026")
      }

      "display past VAT periods correctly" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)
        val extractedText = document.select("#past-schedule").text()

        response.status shouldBe Status.OK
        
        extractedText should include("VAT period: 1 February 2024 to 30 April 2024")
        extractedText should include("VAT period: 1 May 2024 to 31 July 2024")
        extractedText should include("VAT period: 1 August 2024 to 31 October 2024")
        extractedText should include("VAT period: 1 November 2024 to 31 January 2025")
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