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

package connectors

import controllers.ControllerBaseSpec
import models.{TTPRequestModel, TTPResponseModel}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import scala.concurrent.ExecutionContext

class TimeToPayConnectorSpec extends ControllerBaseSpec{

  "TimeToPayConnector" should {

    val connector = new TimeToPayConnector(mock[HttpClient], mockAppConfig)
    val requestModel = TTPRequestModel("/return-url", "/back-url")
    val responseModel = TTPResponseModel("592d4a09cdc8e04b00021459", "www.TestWebsite.co.uk")

    "return a TTPResponeModel" in {
      connector.setupJourney(requestModel)(_: HeaderCarrier, _: ExecutionContext ) shouldEqual responseModel
    }



  }

}
