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

package connectors

import controllers.ControllerBaseSpec
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import mocks.MockMetricsService



class DDConnectorSpec extends ControllerBaseSpec {

  "DirectDebitConnector" should {
    "generate the correct payments url" in {
      val connector = new DDConnector(mock[HttpClient], mockAppConfig, MockMetricsService)
      connector.setupUrl shouldEqual "direct-debit-backend/start-journey"
    }
  }


}
