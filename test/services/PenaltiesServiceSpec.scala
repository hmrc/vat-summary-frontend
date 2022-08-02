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

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.penalties.PenaltiesSummary
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import common.TestModels.penaltySummaryResponse
import config.AppConfig
import connectors.PenaltiesConnector
import mocks.MockAppConfig
import models.errors.PenaltiesFeatureSwitchError
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class PenaltiesServiceSpec extends AnyWordSpecLike with MockFactory with Matchers with GuiceOneAppPerSuite {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)

  val mockPenaltiesConnector: PenaltiesConnector = mock[PenaltiesConnector]

  def setup(penaltiesSummary: HttpGetResult[PenaltiesSummary]): Any =
    (mockPenaltiesConnector.getPenaltiesDataForVRN(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*,*,*)
      .returns(Future.successful(penaltiesSummary))

  def penaltiesService() : PenaltiesService = {
    new PenaltiesService(mockPenaltiesConnector)
  }

  "Calling getPenaltiesDataForVRN when the feature switch is enabled" should {
    "retrieve the penalties summary for the vrn" in {
      mockAppConfig.features.penaltiesAndInterestWYOEnabled(true)
      val summary: HttpGetResult[PenaltiesSummary] = await{
        setup(penaltySummaryResponse)
        penaltiesService().getPenaltiesInformation("123")
      }
      summary shouldBe penaltySummaryResponse
    }
  }

  "Calling getPenaltiesDataForVRN when the feature switch is disabled" should {
    "return Penalties feature switch error" in {
      mockAppConfig.features.penaltiesAndInterestWYOEnabled(false)
      val summary: HttpGetResult[PenaltiesSummary] = await(penaltiesService().getPenaltiesInformation("123"))
      summary shouldBe Left(PenaltiesFeatureSwitchError)
    }
  }


}
