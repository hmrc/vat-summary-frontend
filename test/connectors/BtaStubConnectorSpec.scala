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
import play.api.http.Status._
import play.twirl.api.Html
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial

import scala.concurrent.{ExecutionContext, Future}

class BtaStubConnectorSpec extends ControllerBaseSpec {

  lazy val hc: VatHeaderCarrierForPartialsConverter = injector.instanceOf[VatHeaderCarrierForPartialsConverter]

  def setup(response: HttpResponse): BtaStubConnector = {
    val mockHttp = mock[HttpClient]

    def generateResponse(res: HttpResponse): Future[HttpResponse] = {
      if(res.status >= 200 && res.status <= 399) {
        Future.successful(res)
      } else if(res.status >= 400 && res.status <= 499) {
        Future.failed(Upstream4xxResponse(
          message = res.body,
          upstreamResponseCode = res.status,
          reportAs = res.status
        ))
      } else {
        Future.failed(Upstream5xxResponse(
          message = res.body,
          upstreamResponseCode = res.status,
          reportAs = res.status
        ))
      }
    }

    (mockHttp.GET[HttpResponse](_: String)(_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(generateResponse(response))

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
  }
}
