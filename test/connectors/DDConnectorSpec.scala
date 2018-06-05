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

import com.codahale.metrics.Clock.CpuTimeClock
import com.codahale.metrics.Timer
import connectors.httpParsers.ResponseHttpParsers.HttpPostResult
import controllers.{ControllerBaseSpec, routes}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import mocks.MockMetricsService
import models.errors.HttpError
import play.api.libs.json.{JsObject, Json, Writes}
import org.scalatest.concurrent.ScalaFutures._
import play.api.http.Status
import uk.gov.hmrc.audit.handler.HttpResult
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.{ExecutionContext, Future}

class DDConnectorSpec extends ControllerBaseSpec {

  "DirectDebitConnector" should {
    "connect with direct debit service" in {

      implicit val hc = HeaderCarrier()
      import connectors.httpParsers.DDRedirectUrlHttpParser.DDRedirectUrlReads
      val vrn = "123456789"
      val backUrl: String = routes.OpenPaymentsController.openPayments().url
      val returnUrl: String = routes.VatDetailsController.details().url
      val jsonIn = Json.obj(
        "userId" -> vrn,
        "userIdType" -> "VRN",
        "returnUrl" -> returnUrl,
        "backUrl" -> backUrl
      )

      val nextUrl = "http://next.url/123"
//
//
//      val c = new CpuTimeClock
//      val t = new Timer.Context(c)
//      (() => MockMetricsService.timer.time()).expects().returning(t)


      val httpResponse: Future[HttpPostResult[String]] = Right(nextUrl)

      val client = mock[HttpClient]



      (client
        .POST[JsObject, HttpPostResult[String]](
        _: String,
        _: JsObject,
        _: Seq[(String, String)])(
        _: Writes[JsObject],
        _: HttpReads[HttpPostResult[String]],
        _: HeaderCarrier,
        _: ExecutionContext)
      )
        .expects("/direct-debit-backend/start-journey", jsonIn, *, *, *, *, *)
        .returns(httpResponse)


      val connector = new DDConnector(client, mockAppConfig, MockMetricsService)

      val result: HttpPostResult[String] = connector.startJourney(vrn).futureValue
      result shouldBe Right(nextUrl)


      //connector.createBody(vrn: String, backUrl: String, returnUrl: String) shouldEqual Json.obj("")
    }


  }



}
