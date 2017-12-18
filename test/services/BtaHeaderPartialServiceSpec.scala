/*
 * Copyright 2017 HM Revenue & Customs
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

import scala.concurrent.Future
import connectors.BtaHeaderPartialConnector
import controllers.ControllerBaseSpec
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

class BtaHeaderPartialServiceSpec extends ControllerBaseSpec {

  private trait Test {
    val htmlResult: Html
    val mockBtaHeaderPartialConnector: BtaHeaderPartialConnector = mock[BtaHeaderPartialConnector]

    def setup(): Any = {
      (mockBtaHeaderPartialConnector.getBtaHeaderPartial()(_: Request[AnyContent]))
        .expects(*)
        .returns(Future.successful(htmlResult))
    }

    lazy val service: BtaHeaderPartialService = {
      setup()
      new BtaHeaderPartialService(mockBtaHeaderPartialConnector)
    }
  }

  "The BtaHeaderPartialService.btaHeaderPartial" when {

    "valid HTML is retrieved from the connector" should {

      "return the expected HTML" in new Test {
        override val htmlResult: Html = Html("<div>example</div>")
        val result: Html = await(service.btaHeaderPartial())

        result shouldBe htmlResult
      }
    }

    "no HTML is retrieved from the connector" should {

      "return blank HTML" in new Test {
        override val htmlResult: Html = Html("")
        val result: Html = await(service.btaHeaderPartial())

        result shouldBe htmlResult
      }
    }
  }
}
