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

class PaymentsOnAccountPageSpec2 extends PaymentsOnAccountPageBaseSpec {
  
  override def servicesConfig: Map[String, String] = super.servicesConfig ++ Map(
    "date-service.staticDate.value" -> "2025-02-25"
  )

  "Calling the Payments On Account route as an authenticated user" when {

    "when there is a balancing payment due with static date 2025-02-25" should {
      "display the correct balancing payment message" in {
        val request = setupRequest(PaymentsOnAccountStub.poaJson)

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.select(balanceMessageSelector).text() shouldBe "Your next payment due is your balancing payment. This is due on the same date as your VAT return due date."
      }
    }
  }
}