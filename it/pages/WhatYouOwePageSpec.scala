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

import config.AppConfig
import helpers.IntegrationBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.CustomerInfoStub.customerInfoJson
import stubs.PenaltyDetailsStub.penaltyDetailsJsonMin
import stubs.TimeToPayStub.timeToPayResponseJson
import stubs.{AuthStub, CustomerInfoStub, FinancialDataStub, PenaltyDetailsStub, ServiceInfoStub, TimeToPayStub}

class WhatYouOwePageSpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  def setupRequest(): WSRequest = {
    AuthStub.authorised()
    CustomerInfoStub.stubCustomerInfo(customerInfoJson(isPartialMigration = false, hasVerifiedEmail = true))
    ServiceInfoStub.stubServiceInfoPartial
    buildRequest("/what-you-owe")
  }

  val totalAmountSelector = "p.govuk-body:nth-of-type(2)"
  val chargeRowsSelector = "tr.govuk-table__row > td > a"

  "Calling the What You Owe route as an authenticated user" when {

    "the user has outstanding charges" when {

      "the user has LPP charges and corresponding LPP details" should {

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
          document.select(totalAmountSelector).text() shouldBe "£30,721.09"
          document.select(chargeRowsSelector).size() shouldBe 9
        }
      }

      "the user has no LPP charges or LPP details" should {

        "load the page, rendering appropriate content such as the total amount due and the table of charges" in {
          val request = {
            FinancialDataStub.stubSingleCharge
            PenaltyDetailsStub.stubPenaltyDetails(Status.OK, penaltyDetailsJsonMin)
            setupRequest()
          }

          val response: WSResponse = await(request.get())
          val document: Document = Jsoup.parse(response.body)

          response.status shouldBe Status.OK
          document.title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
          document.select(totalAmountSelector).text() shouldBe "£10,000.00"
          document.select(chargeRowsSelector).size() shouldBe 1
        }
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

    "the user has a partial migration" should {

      "redirect to /vat-overview" in {

        val request = {
          AuthStub.authorised()
          CustomerInfoStub.stubCustomerInfo(customerInfoJson(isPartialMigration = true, hasVerifiedEmail = true))
          buildRequest("/what-you-owe")
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/vat-through-software/vat-overview"
      }
    }

    "the user has the required request response and clicks on the TTP link" should {

      "redirect to /time-to-pay" in {

        val request = {
          appConfig.features.overdueTimeToPayDescriptionEnabled(true)
          AuthStub.authorised()
          TimeToPayStub.stubESSTTPBackend(Status.CREATED, timeToPayResponseJson)
          buildRequest("/time-to-pay")
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/time-to-pay"
      }
    }
  }
}
