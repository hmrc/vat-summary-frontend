/*
 * Copyright 2021 HM Revenue & Customs
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

package testOnly.connectors

import config.VatHeaderCarrierForPartialsConverter
import controllers.ControllerBaseSpec
import play.api.http.Status._
import play.twirl.api.Html
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial

import scala.concurrent.{ExecutionContext, Future}

class BtaStubConnectorSpec extends ControllerBaseSpec {

  lazy val hc: VatHeaderCarrierForPartialsConverter = injector.instanceOf[VatHeaderCarrierForPartialsConverter]
  lazy val testPartial: String = mockAppConfig.viewVatPartial

  def setup(response: HttpResponse): BtaStubConnector = {
    val mockHttp = mock[HttpClient]

    def generateResponse(res: HttpResponse): Future[HttpResponse] = {
      if(res.status >= 200 && res.status <= 399) {
        Future.successful(res)
      } else if(res.status >= 400 && res.status <= 499) {
        Future.failed(UpstreamErrorResponse(
          message = res.body,
          statusCode = res.status,
          reportAs = res.status
        ))
      } else {
        Future.failed(UpstreamErrorResponse(
          message = res.body,
          statusCode = res.status,
          reportAs = res.status
        ))
      }
    }

    (mockHttp.GET[HttpResponse](_: String, _: Seq[(String, String)], _: Seq[(String, String)])
      (_: HttpReads[HttpResponse], _: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *, *, *)
      .returns(generateResponse(response))

    new BtaStubConnector(mockHttp, hc, ec)
  }

  "PartialsConnector .getPartial" when {

    "200 is returned" should {

      lazy val connector = setup(HttpResponse(OK, "content"))

      lazy val result = connector.getPartial(testPartial)

      "return HtmlPartial Success with html content" in {
        await(result) shouldEqual HtmlPartial.Success(None, Html("content"))
      }
    }

    "401 is returned" should {

      lazy val connector = setup(HttpResponse(UNAUTHORIZED, "response"))

      lazy val result = connector.getPartial(testPartial)

      "return HtmlPartial Failure" in {
        await(result) shouldEqual HtmlPartial.Failure(Some(UNAUTHORIZED), "response")
      }
    }

    "403 is returned" should {

      lazy val connector = setup(HttpResponse(FORBIDDEN, "response"))

      lazy val result = connector.getPartial(testPartial)

      "return HtmlPartial Failure" in {
        await(result) shouldEqual HtmlPartial.Failure(Some(FORBIDDEN), "response")
      }
    }
  }
}
