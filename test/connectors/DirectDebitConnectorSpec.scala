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
import mocks.MockMetricsService
import uk.gov.hmrc.play.bootstrap.http.HttpClient


class DirectDebitConnectorSpec extends ControllerBaseSpec{

  "DirectDebitConnector" should {
    "generate the correct check direct debit end point url" when {

      "directDebitDummyPage feature switch is off" in {

        mockAppConfig.features.useDirectDebitDummyPage(false)
        val connector = new DirectDebitConnector(mock[HttpClient], mockAppConfig, MockMetricsService)
        connector.setupUrl shouldEqual "direct-debits-url/direct-debit/start"
      }

      "directDebitDummyPage feature switch is on" in {

        mockAppConfig.features.useDirectDebitDummyPage(true)
        val connector = new DirectDebitConnector(mock[HttpClient], mockAppConfig, MockMetricsService)
        connector.setupUrl shouldEqual "/vat-through-software/test-only/direct-debit-backend/start-journey"
      }
    }
  }
}
