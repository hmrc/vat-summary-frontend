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

package connectors

import config.VatHeaderCarrierForPartialsConverter
import controllers.ControllerBaseSpec
import play.twirl.api.Html
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial

import scala.concurrent.{ExecutionContext, Future}

class BtaStubConnectorSpec extends ControllerBaseSpec {

  lazy val hc: VatHeaderCarrierForPartialsConverter = injector.instanceOf[VatHeaderCarrierForPartialsConverter]

  def setup(response: HttpResponse): BtaStubConnector = {
    val mockHttp = mock[HttpClient]

    (mockHttp.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(Future.successful(response))

    new BtaStubConnector(mockHttp, mockAppConfig, hc)
  }

  "PartialsConnector .getPartial" when {

    "200 is returned" should {

      lazy val connector = setup(HttpResponse(OK, responseString = Some("content")))

      lazy val result = connector.getPartial

      "return HtmlPartial Success with html content" in {
        await(result) shouldEqual HtmlPartial.Success(None, Html("content"))
      }
    }

    "401 is returned" should {

      lazy val connector = setup(HttpResponse(UNAUTHORIZED, responseString = Some("response")))

      lazy val result = connector.getPartial

      "return HtmlPartial Failure" in {
        await(result) shouldEqual HtmlPartial.Failure(Some(UNAUTHORIZED), "response")
      }
    }

    "403 is returned" should {

      lazy val connector = setup(HttpResponse(FORBIDDEN, responseString = Some("response")))

      lazy val result = connector.getPartial

      "return HtmlPartial Failure" in {
        await(result) shouldEqual HtmlPartial.Failure(Some(FORBIDDEN), "response")
      }
    }

    "500 is returned " should {

      lazy val connector = setup(HttpResponse(INTERNAL_SERVER_ERROR, responseString = Some("")))

      lazy val result = connector.getPartial

      "return HtmlPartial Failure" in {
        await(result) shouldEqual HtmlPartial.Failure(Some(INTERNAL_SERVER_ERROR), "")
      }
    }
  }
}
