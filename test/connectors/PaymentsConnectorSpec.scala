/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.http.HttpClient

class PaymentsConnectorSpec extends ControllerBaseSpec {

  "The payments connector" should {

    val connector = new PaymentsConnector(mock[HttpClient], mockAppConfig, MockMetricsService)

    "generate the correct endpoint URL" in {

      connector.vatUrl("vat-return-debit-charge") shouldEqual
        "/pay-api/view-and-change/vat/vat-return-debit-charge/journey/start"

    }
  }
}
