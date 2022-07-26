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

package services

import common.TestModels.penaltyDetailsResponse
import connectors.PenaltyDetailsConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.penalties.PenaltyDetails
import uk.gov.hmrc.http.HeaderCarrier
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsServiceSpec extends AnyWordSpecLike with MockFactory with Matchers {

  val mockPenaltyDetailsConnector = mock[PenaltyDetailsConnector]
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def setup(penaltyDetails: HttpGetResult[PenaltyDetails]): Any =
    (mockPenaltyDetailsConnector.getPenaltyDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*,*,*)
      .returns(Future.successful(penaltyDetails))

  val service: PenaltyDetailsService = {
    setup(penaltyDetailsResponse)
    new PenaltyDetailsService(mockPenaltyDetailsConnector)
  }

  "Calling PenaltyDetailsService.getPenaltyDetails" should {

    "return the PenaltyDetails that the connector returns" in {

      await(service.getPenaltyDetails("123")) shouldBe penaltyDetailsResponse

    }
  }

}
