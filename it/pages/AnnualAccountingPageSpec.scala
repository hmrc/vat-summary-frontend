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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.PaymentsOnAccountStub

class AnnualAccountingPageSpec extends AnnualAccountingPageBaseSpec {

  override def servicesConfig: Map[String, String] = super.servicesConfig ++ Map(
    "date-service.staticDate.value" -> "2025-11-01",
    "features.annualAccountingFeature.enabled" -> "true"
  )

  "GET /interim-payments" should {
    "render the page with next upcoming payment message" in {
      val request = setupRequest(PaymentsOnAccountStub.poaJson)
      val response: WSResponse = await(request.get())
      val document: Document = Jsoup.parse(response.body)

      response.status shouldBe Status.OK
      document.title() shouldBe "Interim payments - Manage your VAT account - GOV.UK"
      document.select("#next-payment-text").text() should include ("Your next upcoming payment")
    }

    "return internal server error when SR API fails" in {
      val request = setupRequest(PaymentsOnAccountStub.errorJson, Status.INTERNAL_SERVER_ERROR)
      val response: WSResponse = await(request.get())
      response.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }
}


