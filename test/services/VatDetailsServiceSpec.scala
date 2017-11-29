/*
 * Copyright 2017 HM Revenue & Customs
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

package services

import java.time.LocalDate

import connectors.VatApiConnector
import connectors.httpParsers.ObligationsHttpParser._
import controllers.ControllerBaseSpec
import models.errors.BadRequestError
import models.{Obligation, Obligations, User}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}

class VatDetailsServiceSpec extends ControllerBaseSpec {

  private trait Test {
    val currentObligation: Obligation = Obligation(
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-30"),
      due = LocalDate.parse("2017-04-30"),
      "O",
      None,
      "#001"
    )

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockConnector: VatApiConnector = mock[VatApiConnector]
    lazy val service = new VatDetailsService(mockConnector)
  }

  "Calling .retrieveNextReturnObligation" when {

    "sequence contains one obligation" should {

      "return current obligation" in new Test {
        val obligations = Obligations(Seq(currentObligation))
        lazy val result: Option[Obligation] =
          service.retrieveNextReturnObligation(obligations, LocalDate.parse("2017-03-30"))

        result shouldBe Some(currentObligation)
      }
    }

    "sequence contains more than one obligation" should {

      val futureObligation: Obligation = Obligation(
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-03-30"),
        due = LocalDate.parse("2017-07-30"),
        "O",
        None,
        "#001"
      )

      "return the obligation which is due today" in new Test {
        val obligations = Obligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[Obligation] =
          service.retrieveNextReturnObligation(obligations, LocalDate.parse("2017-04-30"))

        result shouldBe Some(currentObligation)
      }

      "return the obligation which is due in the future" in new Test {
        val obligations = Obligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[Obligation] =
          service.retrieveNextReturnObligation(obligations, LocalDate.parse("2017-05-30"))

        result shouldBe Some(futureObligation)
      }

      "return the most recent overdue obligation" in new Test {
        val obligations = Obligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[Obligation] =
          service.retrieveNextReturnObligation(obligations, LocalDate.parse("2017-08-30"))

        result shouldBe Some(futureObligation)
      }
    }
  }

  "Calling .getVatDetails" when {

    "the connector returns some obligations" should {

      "return the most recent outstanding obligation" in new Test {

        (mockConnector.getObligations(_:String,_:LocalDate,_:LocalDate,_: Obligation.Status.Value)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(Obligations(Seq(currentObligation)))))

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe Right(Some(currentObligation))
      }

    }

    "the connector returns no obligations" should {

      "return nothing" in new Test {

        (mockConnector.getObligations(_:String,_:LocalDate,_:LocalDate,_: Obligation.Status.Value)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(Obligations(Seq.empty))))

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe Right(None)
      }

    }

    "the connector returns an HttpError" should {

      "return a Future containing the error" in new Test {
        (mockConnector.getObligations(_:String,_:LocalDate,_:LocalDate,_: Obligation.Status.Value)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Left(BadRequestError("TEST_FAIL", "this is a test"))))

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe Left(BadRequestError("TEST_FAIL", "this is a test"))
      }

    }

    "the connector returns an Exception" should {

      "return a failed Future containing the exception" in new Test {
        val expected = new RuntimeException("test")
        (mockConnector.getObligations(_:String, _:LocalDate, _:LocalDate, _: Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.failed(expected))

        intercept[RuntimeException](await(service.getVatDetails(User("1111"))))
      }

    }

  }

}
