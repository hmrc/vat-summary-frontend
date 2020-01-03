/*
 * Copyright 2020 HM Revenue & Customs
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

import connectors.ServiceInfoPartialConnector
import controllers.ControllerBaseSpec
import models.User
import play.api.mvc.Request
import play.twirl.api.{Html, HtmlFormat}

import scala.concurrent.{ExecutionContext, Future}

class ServiceInfoServiceSpec extends ControllerBaseSpec {

  val mockConnector: ServiceInfoPartialConnector = mock[ServiceInfoPartialConnector]
  val service: ServiceInfoService = new ServiceInfoService(mockConnector)
  val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  val user: User = User("1231231231")
  val agentUser: User = User("1231231231", arn = Some("XAIT123123123"))
  val validHtml = Html("<nav>btalink<nav>")
  val htmlError = Html("error")

  "getServiceInfo Partial" should {
    "return bta Partial" in {
      (mockConnector.getServiceInfoPartial()(_:Request[_], _:ExecutionContext))
        .expects(*, *)
        .returning(Future.successful(validHtml))

      val result: Html = await(service.getPartial(fakeRequest, user, ec))
      val expectedResult: Html = validHtml

      result shouldBe expectedResult
    }
    "return empty HTML for an agent" in {
      (mockConnector.getServiceInfoPartial()(_:Request[_], _:ExecutionContext))
        .expects(*, *)
        .never()

      val result: Html = await(service.getPartial(fakeRequest, agentUser, ec))
      val expectedResult: Html = HtmlFormat.empty

      result shouldBe expectedResult
    }
  }

}
