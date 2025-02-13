/*
 * Copyright 2025 HM Revenue & Customs
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

import common.TestModels.{customerInformationMax, directDebitEnrolled, payment}
import forms.ExistingDirectDebitFormProvider
import models.User
import models.payments.Payments
import org.jsoup.Jsoup
import play.api.data.Form
import play.api.http.Status.OK
import play.api.mvc.MessagesControllerComponents
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.payments.ExistingDirectDebit
import common.TestModels._
import models.User
import models.errors.{PaymentsError, UnexpectedStatusError}
import models.payments._
import models.viewModels._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.mockito.internal.verification.VerificationModeFactory.times
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

import scala.concurrent.Future


class ExistingDirectDebitControllerSpec extends ControllerBaseSpec {

  val existingDirectDebit: ExistingDirectDebit = injector.instanceOf[ExistingDirectDebit]
  val ddForm: ExistingDirectDebitFormProvider = new ExistingDirectDebitFormProvider()

  implicit val user: User = User("111111111")
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val dueDateOrUrl: String = "2017-12-07"
  val linkId: String = "wyo"
  val ddStatus: Boolean = true

  val controller = new ExistingDirectDebitController(
    authorisedController,
    mcc,
    mockServiceInfoService,
    existingDirectDebit,
    ddForm
  )


  "The ExistingDirectDebitController .show method" when {

    "a principal user is authenticated" when {

      "the user has DD setup " when {

        lazy val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockServiceInfoCall()
          controller.show(dueDateOrUrl, linkId, ddStatus)(fakeRequest.withMethod("GET"))
        }

        "return OK" in {
          status(result) shouldBe OK
        }

        "return the correct content" in {
          Jsoup.parse(contentAsString(result)).title() shouldBe "You have a Direct Debit Instruction in place - Manage your VAT account - GOV.UK"
        }

      }

    }
  }

  "The ExistingDirectDebitController .submit method from wyo page" when {

    "a principal user is authenticated" when {

      "redirect to the payment page when user clicked yes and form submitted from wyo page" in {

        val postRequest = fakePostRequest.withFormUrlEncodedBody(("value", ExistingDDContinuePayment.Yes.toString),
          ("dueDateOrUrl",dueDateOrUrl), ("linkId",linkId), ("directDebitMandateFound", ddStatus.toString)).withMethod("POST")

        lazy val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockServiceInfoCall()
          controller.submit()(postRequest)
        }
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/vat-through-software/make-payment/vat/generic?earliestDueDate=2017-12-07&linkId=existing-dd-pay-now-button")
      }
    }
  }


  "The ExistingDirectDebitController .submit method from charge-detail page" when {

    "a principal user is authenticated" when {

      "redirect to the payment page when user clicked yes and form submitted from wyo page" in {

        val chargeDetailMakePaymentUrl = " /vat-through-software/make-payment/50000/7/2017/2017-07-31/VAT%20Return%20Debit%20Charge/2017-12-07/VAT-RTN-DEBIT-2"

        val postRequest = fakePostRequest.withFormUrlEncodedBody(("value", ExistingDDContinuePayment.Yes.toString),
          ("dueDateOrUrl", chargeDetailMakePaymentUrl), ("linkId","charge-breakdown"), ("directDebitMandateFound", ddStatus.toString)).withMethod("POST")

        lazy val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockServiceInfoCall()
          controller.submit()(postRequest)
        }
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(chargeDetailMakePaymentUrl)
      }
    }
  }


  "The ExistingDirectDebitController .submit method from wyo page" when {

    "a principal user is authenticated" when {

      "redirect to the wyo page when user clicked No and form submitted from wyo page" in {

        val postRequest = fakePostRequest.withFormUrlEncodedBody(("value", ExistingDDContinuePayment.No.toString),
          ("dueDateOrUrl",dueDateOrUrl), ("linkId",linkId), ("directDebitMandateFound", ddStatus.toString)).withMethod("POST")

        lazy val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockServiceInfoCall()
          controller.submit()(postRequest)
        }
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/vat-through-software/what-you-owe")
      }
    }
  }


  "The ExistingDirectDebitController .submit method from wyo page without selecting yes or no" when {

    "a principal user is authenticated" when {

      "redirect to error page when user not selected any option and form submitted from wyo page" in {

        val postRequest = fakePostRequest.withFormUrlEncodedBody(
          ("dueDateOrUrl",dueDateOrUrl), ("linkId",linkId), ("directDebitMandateFound", ddStatus.toString)).withMethod("POST")

        lazy val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockServiceInfoCall()
          controller.submit()(postRequest)
        }
        status(result) mustBe BAD_REQUEST
      }
    }
  }


}