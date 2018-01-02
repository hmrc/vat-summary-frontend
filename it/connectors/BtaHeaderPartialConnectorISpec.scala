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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.test.FakeRequest
import play.twirl.api.Html
import stubs.BtaHeaderPartialStub
import uk.gov.hmrc.http.HeaderCarrier

class BtaHeaderPartialConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: BtaHeaderPartialConnector = app.injector.instanceOf[BtaHeaderPartialConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "Calling the getBtaHeaderPartial() function" when {

    "a successful status is returned" should {

      "return partial content" in new Test {
        override def setupStubs(): StubMapping = BtaHeaderPartialStub.successfulContent
        setupStubs()

        val expectedContent = Html("<div>example</div>")

        private val result = await(connector.getBtaHeaderPartial()(FakeRequest()))
        result shouldEqual expectedContent
      }
    }

    "a BAD_REQUEST status is returned" should {

      "return an error" in new Test {
        override def setupStubs(): StubMapping = BtaHeaderPartialStub.badRequest
        setupStubs()

        val expectedContent = Html("")

        private val result = await(connector.getBtaHeaderPartial()(FakeRequest()))
        result shouldEqual expectedContent
      }
    }

    "a GATEWAY_TIMEOUT status is returned" should {

      "return an error" in new Test {
        override def setupStubs(): StubMapping = BtaHeaderPartialStub.gatewayTimeout
        setupStubs()

        val expectedContent = Html("")

        private val result = await(connector.getBtaHeaderPartial()(FakeRequest()))
        result shouldEqual expectedContent
      }
    }
  }
}
