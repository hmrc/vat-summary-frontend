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

package testOnly.controllers

import common.TestModels._
import controllers.ControllerBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Helpers._
import views.html.errors.{NotFound, PaymentsError}
import views.html.payments.{ChargeTypeDetailsView, CrystallisedInterestView, CrystallisedLPP1View, EstimatedInterestView}

class ChargeBreakdownControllerSpec extends ControllerBaseSpec {

  val controller = new ChargeBreakdownController(
    authorisedController, mcc, mockServiceInfoService, mockWYOSessionService,
    injector.instanceOf[ChargeTypeDetailsView], injector.instanceOf[EstimatedInterestView],
    injector.instanceOf[PaymentsError], injector.instanceOf[NotFound],
    injector.instanceOf[CrystallisedInterestView], injector.instanceOf[CrystallisedLPP1View]
  )
  val id: String = "1234569"

  "The showBreakdown action" when {

    "the user is logged in as a principal entity" when {

      "the retrieved model is a StandardChargeViewModel" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(Some(wyoStandardDBModel))
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the correct page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("#standard-charge-heading") should exist
        }
      }

      "the retrieved model is an EstimatedInterestViewModel" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(Some(wyoEstimatedIntDBModel))
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the correct page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("#estimated-interest-heading") should exist
        }
      }

      "the retrieved model is a CrystallisedInterestViewModel" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(Some(wyoCrystallisedIntDBModel))
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the correct page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("#crystallised-interest-heading") should exist
        }
      }

      "the retrieved model is a CrystallisedLPP1ViewModel" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(Some(wyoCrystallisedLPP1DBModel))
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the correct page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("#crystallised-lpp1-heading") should exist
        }
      }

      "the retrieved model has an invalid type" should {
        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(Some(wyoDBModel("blah", standardChargeModelMinJson)))
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return ISE (500)" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }


      "a model cannot be retrieved" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockWYOSessionServiceCall(None)
          controller.showBreakdown(id)(fakeRequestWithSession)
        }

        "return not found (404)" in {
          status(result) shouldBe NOT_FOUND
        }
      }
    }

    "the user is logged in as an agent" should {

      lazy val result = {
        mockAgentAuth()
        mockServiceInfoCall()
        mockWYOSessionServiceCall(Some(wyoStandardDBModel))
        controller.showBreakdown(id)(agentFinancialRequest)
      }

      "return 200" in {
        status(result) shouldBe OK
      }

      "load the page" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.title() shouldBe "VAT - Your client’s VAT details - GOV.UK"
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.showBreakdown(id)(fakeRequestWithSession)
      }

      "return status 303" in {
        status(result) shouldBe SEE_OTHER
      }
      "return the correct redirect location which should be sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user has invalid credentials" should {

      "return 403" in {
        mockInsufficientEnrolments()
        val result = controller.showBreakdown(id)(fakeRequestWithSession)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.showBreakdown(id)(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

  }
}
