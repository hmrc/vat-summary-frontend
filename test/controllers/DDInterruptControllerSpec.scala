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

import play.api.http.Status
import common.TestModels.{customerInformationLaterMigratedToETMPDate, customerInformationMax, customerInformationMin}
import models.{CustomerInformation, DDIDetails, DirectDebitStatus, ServiceResponse}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.interrupt.{DDInterruptExistingDD, DDInterruptNoDD}
import play.api.mvc.Result
import play.api.test.Helpers._
import scala.concurrent.{ExecutionContext, Future}

class DDInterruptControllerSpec extends ControllerBaseSpec {

  val relativeUrl = "/homepage"
  val expectedRedirectLocation: Option[String] = Some(relativeUrl)
  val DDNoInterrupt: DDInterruptNoDD = injector.instanceOf[DDInterruptNoDD]
  val DDInterruptExistingDD: DDInterruptExistingDD = injector.instanceOf[DDInterruptExistingDD]
  val controller = new DDInterruptController(
    mockPaymentsService,
    authorisedController,
    mockAccountDetailsService,
    mockDateService,
    mcc,
    DDNoInterrupt,
    DDInterruptExistingDD
  )

  lazy val noDirectDebitSetup: Future[ServiceResponse[DirectDebitStatus]] =
    Future.successful(Right(DirectDebitStatus(directDebitMandateFound = false, None)))
  lazy val directDebitSetup: Future[ServiceResponse[DirectDebitStatus]] =
    Future.successful(Right(DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-03-01"))))))
  lazy val directDebitSetupAfterMigrationDate: Future[ServiceResponse[DirectDebitStatus]] =
    Future.successful(Right(DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-05-01"))))))

  "The user meets the requirements for the DD interrupt Screen and the feature switch is enabled" when {

    "they have no DD" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationLaterMigratedToETMPDate))
        mockDateServiceCall()
        (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *)
          .returns(noDirectDebitSetup)
        controller.directDebitInterruptCall(relativeUrl)(fakeRequest)
      }

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "return no DD interrupt view" in {
        contentAsString(result)
          .contains("You need to set up a new Direct Debit") shouldBe true
      }
    }

    "they have an existing DD but need to validate their bank details and the feature switch is enabled" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationLaterMigratedToETMPDate))
        mockDateServiceCall()
        (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *)
          .returns(directDebitSetup)
        controller.directDebitInterruptCall(relativeUrl)(fakeRequest)
      }

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
      }

      "return the existing DD interrupt view" in {
        contentAsString(result)
          .contains("You need to validate your details for Direct Debit") shouldBe true
      }
    }

    "they have an existing DD that was set up at least a week after the migration date" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationLaterMigratedToETMPDate))
        mockDateServiceCall()
        (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *)
          .returns(directDebitSetupAfterMigrationDate)
        controller.directDebitInterruptCall(relativeUrl)(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }
    }

    "migration date is before 4 months ago" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationMax))
        mockDateServiceCall()
        controller.directDebitInterruptCall(relativeUrl)(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "return the correct redirect location" in {
        redirectLocation(result) shouldBe expectedRedirectLocation
      }
    }

    "an empty string is passed as a redirect URL" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationMax))
        mockDateServiceCall()
        controller.directDebitInterruptCall("")(fakeRequest)
      }

      "redirect to the VAT overview page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details().url)
      }
    }

  }

  "The DD Interrupt feature switch is disabled" should {

    lazy val result: Future[Result] = {
      mockPrincipalAuth()
      controller.directDebitInterruptCall(relativeUrl)(fakeRequest)
    }

    "return 303" in {
      mockAppConfig.features.directDebitInterrupt(false)
      status(result) shouldBe Status.SEE_OTHER
    }

    "return the correct redirect location" in {
      mockAppConfig.features.directDebitInterrupt(false)
      redirectLocation(result) shouldBe expectedRedirectLocation
    }
  }

  "The extractRedirectUrl() function" when {

    "a valid relative redirect URL is provided" should {

      "return the URL" in {
        controller.extractRedirectUrl("/homepage") shouldBe Some("/homepage")
      }
    }

    "a valid absolute redirect url is provided" should {

      "return the URL" in {
        controller.extractRedirectUrl("http://localhost:9149/homepage") shouldBe Some("http://localhost:9149/homepage")
      }
    }

    "an invalid redirect url is provided" should {

      "return None" in {
        controller.extractRedirectUrl("http://wwww.google.com") shouldBe None
      }
    }

    "an exception is thrown when trying to construct a continue url" should {

      "return None" in {
        controller.extractRedirectUrl("99") shouldBe None
      }
    }

    "the redirect URL is an empty string" should {

      "return None" in {
        controller.extractRedirectUrl("") shouldBe None
      }
    }
  }

  "The migratedWithin4M function" when {

    "the migration date is less than 4 months ago" should {

      "return true" in {
        val result = {
          mockDateServiceCall()
          controller.migratedWithin4M(customerInformationLaterMigratedToETMPDate)
        }
        result shouldBe true
      }
    }

    "the migration date is greater than 4 months ago" should {

      "return false" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(customerMigratedToETMPDate = Some("2017-06-01"))
        val result = {
          mockDateServiceCall()
          controller.migratedWithin4M(customerInfo)
        }
        result shouldBe false
      }
    }

    "the migration date is exactly 4 months ago" should {

      "return false" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-01-01"))
        val result = {
          mockDateServiceCall()
          controller.migratedWithin4M(customerInfo)
        }
        result shouldBe false
      }
    }

    "there is no customerMigratedToETMPDate" should {

      "return false" in {
        controller.migratedWithin4M(customerInformationMin) shouldBe false
      }
    }
  }

  "The dateBeforeOrWithin7Days function" should {

    val customerInfo = customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-01-01"))

    "return true" when {

      "the DD date is before the migration date" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2017-04-01"))))

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe true
      }

      "the DD date is the same day as the migration date" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-01-01"))))

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe true
      }

      "the DD date is less than a week after the migration date" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-01-04"))))

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe true
      }

      "the DD date is exactly 7 days after the migration date" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-01-08"))))

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe true
      }
    }

    "return false" when {

      "the DD date is greater than a week after the migration date" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-01-09"))))

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe false
      }

      "either the migration date or the DD dates are not retrieved" in {
        val ddStatus: DirectDebitStatus =
          DirectDebitStatus(directDebitMandateFound = true, None)

        controller.dateBeforeOrWithin7Days(customerInfo, ddStatus) shouldBe false
      }
    }
  }
}
