/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import common.SessionKeys
import common.TestModels._
import models.errors.BadRequestError
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._
import views.html.certificate.VatCertificate

class VatCertificateControllerSpec extends ControllerBaseSpec {

  val vatCertificate: VatCertificate = injector.instanceOf[VatCertificate]
  val controller = new VatCertificateController(
    mockServiceInfoService,
    authorisedController,
    mockAccountDetailsService,
    mcc,
    vatCertificate,
    mockServiceErrorHandler
  )

  "The show() action" when {

    "the user is a principal entity" when {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockCustomerInfo(Right(customerInformationMax))
        controller.show()(fakeRequest)
      }

      "return OK (200)" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
      }
    }

    "the user is an agent" when {

      lazy val result = {
        mockAgentAuth()
        mockServiceInfoCall()
        mockCustomerInfo(Right(customerInformationMax))
        controller.show()(fakeRequest.withSession(SessionKeys.mtdVatvcClientVrn -> "123456789"))
      }

      "return OK (200)" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
      }
    }

    "the user is logged in with invalid credentials" should {

      lazy val result = {
        mockInsufficientEnrolments()
        controller.show()(fakeRequest)
      }

      "return Forbidden (403)" in {
        status(result) shouldBe Status.FORBIDDEN
      }

      "return the unauthorised page" in {
        Jsoup.parse(contentAsString(result)).title() shouldBe "You are not authorised to use this service - VAT - GOV.UK"
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.show()(fakeRequest)
      }

      "return SEE_OTHER" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the account details service returns a Left" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockCustomerInfo(Left(BadRequestError("", "")))
        controller.show()(fakeRequest)
      }

      "return ISE (500)" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
