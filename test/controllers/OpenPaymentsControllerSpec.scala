/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate

import models.User
import models.payments.{Payment, Payments}
import models.viewModels.OpenPaymentsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.Result
import services.{DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class OpenPaymentsControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    def setupMocks(): Unit = {
      (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
    }

    val payment = Payment(
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-01-01"),
      BigDecimal("10000"),
      "ABCD"
    )

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockDateService: DateService = mock[DateService]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def target: OpenPaymentsController = {
      setupMocks()
      new OpenPaymentsController(messages, mockEnrolmentsAuthService, mockPaymentsService, mockDateService, mockAppConfig)
    }
  }

  "Calling the openPayments actions" when {

    "the user has open payments" should {

      "return the payments view" in new Test {
        override def setupMocks(): Unit = {
          super.setupMocks()
          (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Some(Payments(Seq(payment, payment)))))
        }

        val result: Result = await(target.openPayments()(fakeRequest))
        val document: Document = Jsoup.parse(bodyOf(result))

        document.select("h1").first().text() shouldBe "What you owe"
      }
    }

    "the user has no open payments" should {

      "return the no payments view" in new Test {
        override def setupMocks(): Unit = {
          super.setupMocks()
          (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Some(Payments(Seq.empty))))
        }

        val result: Result = await(target.openPayments()(fakeRequest))
        val document: Document = Jsoup.parse(bodyOf(result))

        document.select("h1").first().text() shouldBe "What you owe"
      }
    }

    "an error occurs upstream" should {

      "return the payments error view" in new Test {
        override def setupMocks(): Unit = {
          super.setupMocks()
          (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(None))
        }

        val result: Result = await(target.openPayments()(fakeRequest))

        val document: Document = Jsoup.parse(bodyOf(result))

        document.select("h1").first().text() shouldBe "We can't let you pay here right now"
      }
    }
  }

  "Calling the .getModel function" should {

    "return a sequence of OpenPaymentsModel" in new Test {
      override def setupMocks(): Unit = (
        mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01")
      )

      val expected = Seq(OpenPaymentsModel(
        "Return",
        payment.outstandingAmount,
        payment.due,
        payment.start,
        payment.end,
        payment.periodKey,
        overdue = true
      ))
      val result: Seq[OpenPaymentsModel] = target.getModel(Seq(payment))

      result shouldBe expected
    }
  }
}
