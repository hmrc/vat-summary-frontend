/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.CustomerInfoStub.customerInfoJson
import stubs.{AuthStub, CustomerInfoStub, FinancialDataStub, PenaltyDetailsStub, ServiceInfoStub}

class WhatYouOwePageSpec extends IntegrationBaseSpec {

  def setupRequest(): WSRequest = {
    AuthStub.authorised()
    CustomerInfoStub.stubCustomerInfo(customerInfoJson(isPartialMigration = false, hasVerifiedEmail = true))
    ServiceInfoStub.stubServiceInfoPartial
    buildRequest("/test-only/what-you-owe")
  }

  val totalAmountSelector = "p.govuk-body:nth-of-type(2)"
  val chargeRowsSelector = "tr.govuk-table__row > td > a"

  "Calling the What You Owe route as an authenticated user" when {

    "the user has outstanding charges" should {

      "load the page, rendering appropriate content such as the total amount due and the table of charges" in {
        val request = {
          FinancialDataStub.stubOutstandingTransactions
          PenaltyDetailsStub.stubPenaltyDetails()
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
        document.select(totalAmountSelector).text() shouldBe "£20,060.55"
        document.select(chargeRowsSelector).size() shouldBe 5
      }
    }

    "the user has no outstanding charges" should {

      "load the page, not rendering specific charge content such as the total amount due and the table of charges" in {
        val request = {
          FinancialDataStub.stubNoPayments
          setupRequest()
        }

        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
        document.select(totalAmountSelector).size() shouldBe 0
        document.select(chargeRowsSelector).size() shouldBe 0
      }
    }
  }
}
