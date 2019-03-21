/*
 * Copyright 2019 HM Revenue & Customs
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

import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.MandationStatus
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import models.errors.UnknownError

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class MandationStatusServiceSpec extends UnitSpec with MockFactory {

  val mockConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
  val service: MandationStatusService = new MandationStatusService(mockConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val validMandationStatus = MandationStatus(
    "I'mmaWalkingHere"
  )

  "getMandationStatus" should {
    "return a MandationStatus" in {
      (mockConnector.getCustomerMandationStatus(_: String)(_: HeaderCarrier, _:ExecutionContext))
        .expects("1111", *, *)
        .returning(Future.successful(Right(validMandationStatus)))

      val result: HttpGetResult[MandationStatus] = await(service.getMandationStatus("1111"))
      val expectedResult: HttpGetResult[MandationStatus] = Right(validMandationStatus)

      result shouldBe expectedResult
    }

    "return an error if one occurs" in {
      (mockConnector.getCustomerMandationStatus(_: String)(_: HeaderCarrier, _:ExecutionContext))
        .expects("1111", *, *)
        .returning(Future.successful(Left(UnknownError)))

      val result: HttpGetResult[MandationStatus] = await(service.getMandationStatus("1111"))
      val expectedResult: HttpGetResult[MandationStatus] = Left(UnknownError)

      result shouldBe expectedResult
    }
  }
}
