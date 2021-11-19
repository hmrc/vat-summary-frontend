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

package services

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.penalties.PenaltiesSummary
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import common.TestModels.penaltySummaryResponse
import connectors.PenaltiesConnector
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class PenaltiesServiceSpec extends AnyWordSpecLike with MockFactory with Matchers {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val mockPenaltiesConnector: PenaltiesConnector = mock[PenaltiesConnector]

  def setup(penaltiesSummary: HttpGetResult[PenaltiesSummary]): Any =
    (mockPenaltiesConnector.getPenaltiesDataForVRN(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*,*,*)
      .returns(Future.successful(penaltiesSummary))

  def penaltiesService(): PenaltiesService = {
    setup(penaltySummaryResponse)
    new PenaltiesService(mockPenaltiesConnector)
  }

  "Calling getPenaltiesDataForVRN" should {
    "retrieve the penalties summary for the vrn" in {
      val summary: HttpGetResult[PenaltiesSummary] = await(penaltiesService().getPenaltiesInformation("123"))
      summary shouldBe penaltySummaryResponse
    }
  }

}
