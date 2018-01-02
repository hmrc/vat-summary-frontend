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

package services

import connectors.BtaStubConnector
import controllers.ControllerBaseSpec
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.partials.HtmlPartial
import scala.concurrent.Future
import play.api.http.Status._
import play.api.mvc.{AnyContent, Request}

class BtaStubServiceSpec extends ControllerBaseSpec {

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  "Calling BtaStubService .getPartial" when {

    def setup(partial: HtmlPartial): BtaStubService = {
      val mockConnector = mock[BtaStubConnector]

      (mockConnector.getPartial()(_: Request[AnyContent]))
        .expects(*)
        .returns(Future.successful(partial))

      new BtaStubService(mockConnector)
    }

    "request is successful" should {

      lazy val service = setup(HtmlPartial.Success(None, Html("some html")))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("some html")
      }
    }

    "request is unsuccessful with status 401" should {

      lazy val service = setup(HtmlPartial.Failure(Some(UNAUTHORIZED)))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("User is unauthorised")
      }
    }

    "request is unsuccessful with status 403" should {

      lazy val service = setup(HtmlPartial.Failure(Some(FORBIDDEN)))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("User is forbidden")
      }
    }

    "request is unsuccessful with other status code" should {

      lazy val service = setup(HtmlPartial.Failure(Some(INTERNAL_SERVER_ERROR)))
      lazy val result = service.getPartial()

      "return some html" in {
        await(result) shouldEqual Html("Alternative content")
      }
    }
  }

  "Calling BtaStubService .handlePartial" when {

    "partial retrieval is a Success" should {

      val mockConnector = mock[BtaStubConnector]

      val partial = HtmlPartial.Success(None, Html("Success"))
      lazy val service = new BtaStubService(mockConnector)
      lazy val result = service.handlePartial(partial)

      "return html" in {
        await(result.body) shouldEqual "Success"
      }
    }

    "partial retrieval is a Failure" should {

      val mockConnector = mock[BtaStubConnector]

      val partial = HtmlPartial.Failure(Some(INTERNAL_SERVER_ERROR), "")
      lazy val service = new BtaStubService(mockConnector)
      lazy val result = service.handlePartial(partial)

      "return error text" in {
        await(result.body) should include("Alternative content")
      }
    }
  }
}
