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

import java.time.LocalDate

import controllers.ControllerBaseSpec
import models.{Obligation, Obligations}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class VatApiConnectorSpec extends ControllerBaseSpec {

  "VatApiConnector" when {

    "calling the /vat/{vrn}/obligations resource" should {

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val expectedObligations = Obligations(Seq(
        Obligation(
          start = LocalDate.parse("2018-01-01"),
          end = LocalDate.parse("2018-03-31"),
          due = LocalDate.parse("2018-04-30"),
          status = "F",
          received = Some(LocalDate.parse("2018-04-01")),
          periodKey = "#001"
        ),
        Obligation(
          start = LocalDate.parse("2018-03-02"),
          end = LocalDate.parse("2018-06-30"),
          due = LocalDate.parse("2018-07-31"),
          status = "O",
          received = None,
          periodKey = "#002"
        )
      ))

      "return all obligations for a given period" in {
        val mockHttp = mock[HttpClient]
        val connector = new VatApiConnector(mockHttp)

        (mockHttp.GET[HttpResponse](_:String, _:Seq[(String, String)])(_:HttpReads[HttpResponse], _:HeaderCarrier, _:ExecutionContext))
          .expects(connector.obligationsUrl("111111111"),*,*,*,*)
          .returns(Future.successful(HttpResponse(responseStatus = 200, responseJson = Some(Json.toJson(expectedObligations)))))

        val expected = Right(expectedObligations)

        val result = await(connector.getAllObligations(
          "111111111",
          LocalDate.parse("2017-11-23"),
          LocalDate.parse("2018-12-01")
        ))

        result shouldEqual expected

      }

      "return outstanding obligations for a given period" in {
        val mockHttp = mock[HttpClient]
        val connector = new VatApiConnector(mockHttp)

        val expected = Right(Obligations(expectedObligations.obligations.filter(_.status == "O")))

        (mockHttp.GET[HttpResponse](_:String, _:Seq[(String, String)])(_:HttpReads[HttpResponse], _:HeaderCarrier, _:ExecutionContext))
          .expects(connector.obligationsUrl("222222222"),*,*,*,*)
          .returns(Future.successful(HttpResponse(responseStatus = 200, responseJson = Some(Json.toJson(Obligations(expectedObligations.obligations.filter(_.status == "O")))))))

        val result = await(connector.getOutstandingObligations(
          "222222222",
          LocalDate.parse("2017-11-23"),
          LocalDate.parse("2018-12-01")
        ))

        result shouldEqual expected
      }

      "return fulfilled obligations for a given period" in {
        val mockHttp = mock[HttpClient]
        val connector = new VatApiConnector(mockHttp)

        val expected = Right(Obligations(expectedObligations.obligations.filter(_.status == "F")))

        (mockHttp.GET[HttpResponse](_:String, _:Seq[(String, String)])(_:HttpReads[HttpResponse], _:HeaderCarrier, _:ExecutionContext))
          .expects(connector.obligationsUrl("333333333"),*,*,*,*)
          .returns(Future.successful(HttpResponse(responseStatus = 200, responseJson = Some(Json.toJson(Obligations(expectedObligations.obligations.filter(_.status == "F")))))))

        val result = await(connector.getFulfilledObligations(
          "333333333",
          LocalDate.parse("2017-11-23"),
          LocalDate.parse("2018-12-01")
        ))

        result shouldEqual expected
      }

    }

  }

}
