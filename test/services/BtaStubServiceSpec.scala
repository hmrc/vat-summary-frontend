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

import connectors.BtaStubConnector
import controllers.ControllerBaseSpec
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.partials.HtmlPartial
import scala.concurrent.Future
import play.api.http.Status._

class BtaStubServiceSpec extends ControllerBaseSpec {

  def setup(partial: HtmlPartial): BtaStubService = {
    val mockConnector = mock[BtaStubConnector]

    (mockConnector.getPartial()(_: HeaderCarrier))
      .expects(*)
      .returns(Future.successful(partial))

    new BtaStubService(mockConnector)
  }

  "Calling BtaStubService .getPartial" when {

    "request is successful" should {

      implicit val hc: HeaderCarrier = mock[HeaderCarrier]

      lazy val service = setup(HtmlPartial.Success(None, Html("some html")))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("some html")
      }
    }

    "request is unsuccessful" should {

      implicit val hc: HeaderCarrier = mock[HeaderCarrier]

      lazy val service = setup(HtmlPartial.Failure(Some(INTERNAL_SERVER_ERROR)))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("Alternative content")
      }
    }
  }
}
