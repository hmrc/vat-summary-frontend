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

class VatApiConnectorSpec extends ControllerBaseSpec {

  "VatApiConnector" should {

    "generate the correct customer information url" in {

      val connector = new VatApiConnector(mock[HttpClient], mockAppConfig)

      connector.customerInfoUrl("111") shouldEqual "/customer-information/vat/111"

    }
  }
}
