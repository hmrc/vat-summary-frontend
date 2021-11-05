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

package connectors

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.ControllerBaseSpec
import models.penalties.PenaltiesSummary
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.http.HttpClient
import common.TestModels.penaltiesSummaryModel
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.{ExecutionContext, Future}

class PenaltiesConnectorSpec extends ControllerBaseSpec {

  val httpClient: HttpClient = mock[HttpClient]
  val connector: PenaltiesConnector = new PenaltiesConnector(httpClient)(mockAppConfig)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def mockPenaltiesCall(result: Future[HttpGetResult[PenaltiesSummary]]): Any =
    (httpClient.GET[HttpGetResult[PenaltiesSummary]](_: String, _: Seq[(String, String)], _: Seq[(String, String)])
      (_: HttpReads[HttpGetResult[PenaltiesSummary]], _: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *, *, *)
      .returns(result)

  "Calling the penalties service" when {
    "the feature switch is enabled" should {
      "return 200 and a PenaltiesSummary model" in {
        mockAppConfig.features.penaltiesServiceEnabled(true)
        mockPenaltiesCall(Future.successful(Right(penaltiesSummaryModel)))
        await(connector.getPenaltiesDataForVRN("123")) shouldBe Some(Right(penaltiesSummaryModel))
      }
    }

    "when the feature switch is disabled" should {
      "return None" in {
        mockAppConfig.features.penaltiesServiceEnabled(false)
        await(connector.getPenaltiesDataForVRN("123")) shouldBe None
      }
    }
  }
}
