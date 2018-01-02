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

import config.VatHeaderCarrierForPartialsConverter
import controllers.ControllerBaseSpec
import uk.gov.hmrc.play.bootstrap.http.HttpClient

class BtaHeaderPartialConnectorSpec extends ControllerBaseSpec {

  "BtaHeaderPartialConnector" should {

    "generate the correct partial url" in {

      val connector = new BtaHeaderPartialConnector(mock[HttpClient], mockAppConfig, mock[VatHeaderCarrierForPartialsConverter])

      connector.btaUrl shouldEqual "/business-account/partial/service-info"
    }
  }
}
