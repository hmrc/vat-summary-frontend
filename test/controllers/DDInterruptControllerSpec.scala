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
import audit.AuditingService
import audit.models.AuditModel
import common.TestModels.{customerInformationMax, customerInformationMin, successfulAuthResult}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.{CustomerInformation, DDIDetails, DirectDebitStatus, ServiceResponse}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
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

  private trait DirectDebitInterruptTest {
    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult
    val mockAuditService: AuditingService = mock[AuditingService]
    val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
      Future.successful(Right(customerInformationMax))
    val ddResult: Future[ServiceResponse[DirectDebitStatus]] =
      Future.successful(Right(DirectDebitStatus(directDebitMandateFound = false, None)))


    def setup(): Any = {
      mockDateServiceCall()
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})

      (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(accountDetailsServiceResult)

      (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(ddResult)
    }

    def target(): DDInterruptController = {
      setup()
      controller
    }
  }

  "The user meets the requirements for the DD interrupt Screen and the feature switch is enabled" when {

    "they have no DD" which {

      "should present them with the No Direct Debit View" when {

        object Test extends DirectDebitInterruptTest {
          lazy val result: Future[Result] =
            target().directDebitInterruptCall(relativeUrl)(fakeRequest)
          override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
            Future.successful(Right(customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))))
        }

        "return 200" in {
          status(Test.result) shouldBe Status.OK
        }

        "return no DD interrupt view" in {
          contentAsString(Test.result)
            .contains("You need to set up a new Direct Debit") shouldBe true
        }

      }
    }

    "they have an existing DD but need to validate there bank details and the feature switch is enabled" when {

      object Test extends DirectDebitInterruptTest {
        lazy val result: Future[Result] = target().directDebitInterruptCall(relativeUrl)(fakeRequest)
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
          Future.successful(Right(customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))))
        override val ddResult: Future[ServiceResponse[DirectDebitStatus]] =
          Future.successful(Right(DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-03-01"))))))
      }

      "return 200" in {
        status(Test.result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(Test.result) shouldBe Some("text/html")
      }

      "return the existing DD interrupt view" in {
        contentAsString(Test.result)
          .contains("You need to validate your details for Direct Debit") shouldBe true
      }

      "they have an existing DD that was set up at least a week after the migration date" when {
        object Test extends DirectDebitInterruptTest {
          lazy val result: Future[Result] = target().directDebitInterruptCall(relativeUrl)(fakeRequest)
          override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
            Future.successful(Right(customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))))
          override val ddResult: Future[ServiceResponse[DirectDebitStatus]] =
            Future.successful(Right(DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-05-01"))))))
        }
        "return 303" in {
          status(Test.result) shouldBe Status.SEE_OTHER
        }
      }

      "Migration date is older than 4 months" when {

        "redirected to the VAT Overview Page" should {

          object Test extends DirectDebitInterruptTest {
            lazy val result: Future[Result] =
              target().directDebitInterruptCall(relativeUrl)(fakeRequest)
          }

          "return 303" in {
            status(Test.result) shouldBe Status.SEE_OTHER
          }

          "return the correct redirect location" in {
            redirectLocation(Test.result) shouldBe expectedRedirectLocation
          }
        }
      }
    }
  }

  "The DD Interrupt feature switch is disabled" should {
    object Test extends DirectDebitInterruptTest {
      lazy val result: Future[Result] = target().directDebitInterruptCall(relativeUrl)(fakeRequest)
      override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
        Future.successful(Right(customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))))
      override val ddResult: Future[ServiceResponse[DirectDebitStatus]] =
        Future.successful(Right(DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-03-01"))))))
    }

    "return 303" in {
      mockAppConfig.features.directDebitInterrupt(false)
      status(Test.result) shouldBe Status.SEE_OTHER
    }

    "return the correct redirect location" in {
      mockAppConfig.features.directDebitInterrupt(false)
      redirectLocation(Test.result) shouldBe expectedRedirectLocation
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

      "return the URL" in {
        controller.extractRedirectUrl("http://wwww.google.com") shouldBe None
      }
    }

    "an exception is throne when trying to construct a continue url" should {

      "return the URL" in {
        controller.extractRedirectUrl("99") shouldBe None
      }
    }
  }

  "The migratedWithin4M function" when {

    "the migration date is less than 4 months ago" should {

      "return true" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))
        val result = {
          mockDateServiceCall()
          controller.migratedWithin4M(customerInfo)
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
    }
  }
}
