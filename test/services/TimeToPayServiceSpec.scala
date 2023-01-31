/*
 * Copyright 2023 HM Revenue & Customs
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

import common.TestModels.timeToPayResponseModel
import connectors.TimeToPayConnector
import connectors.httpParsers.ResponseHttpParsers.HttpPostResult
import controllers.ControllerBaseSpec
import models.errors.{TimeToPayRedirectError, UnknownError}
import models.TTPRequestModel
import models.essttp.TTPResponseModel
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TimeToPayServiceSpec extends ControllerBaseSpec {

  val hc: HeaderCarrier = HeaderCarrier()
  val mockTTPConnector: TimeToPayConnector = mock[TimeToPayConnector]

  // TODO update return type of this mock; the connector will return a response model not a String
  def mockTTPConnectorCall(response: HttpPostResult[TTPResponseModel]): Any =
    (mockTTPConnector.setupJourney(_: TTPRequestModel)(_: HeaderCarrier, _: ExecutionContext)).expects(*,*,*).returns(Future.successful(response))

  val service = new TimeToPayService(mockTTPConnector)

  "The retrieveRedirectUrl function" should {

    "return a URL when the call is successful" in {
      val result = {
        mockTTPConnectorCall(Right(timeToPayResponseModel))
        service.retrieveRedirectUrl(hc, ec, mockAppConfig)
      }

      await(result) shouldBe Right("www.TestWebsite.co.uk")
    }

    "return an error model when the call is unsuccessful" in {
      val result = {
        mockTTPConnectorCall(Left(UnknownError))
        service.retrieveRedirectUrl(hc, ec, mockAppConfig)
      }

      await(result) shouldBe Left(TimeToPayRedirectError)
    }
  }
}
