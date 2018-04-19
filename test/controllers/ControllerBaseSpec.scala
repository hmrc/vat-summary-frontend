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

package controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import config.AppConfig
import mocks.MockAppConfig
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{CSRFConfigProvider, CSRFFilter}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.play.test.UnitSpec

class ControllerBaseSpec extends UnitSpec with MockFactory with GuiceOneAppPerSuite {

  lazy val injector: Injector = app.injector
  lazy val messages: MessagesApi = injector.instanceOf[MessagesApi]
  implicit val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(
    SessionKeys.lastRequestTimestamp -> "1498236506662", SessionKeys.authToken -> "Bearer Token")

  def fakeRequestToPOSTWithSession(input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequestWithSession.withFormUrlEncodedBody(input: _*)

  implicit class CSRFTokenAdder[T](req: FakeRequest[T]) {

    def addToken(): FakeRequest[T] = {

      val csrfConfig = app.injector.instanceOf[CSRFConfigProvider].get
      val csrfFilter = app.injector.instanceOf[CSRFFilter]
      val token = csrfFilter.tokenProvider.generateToken

      req.copyFakeRequest(tags = req.tags ++ Map(
        Token.NameRequestTag -> csrfConfig.tokenName,
        Token.RequestTag -> token
      )).withHeaders(csrfConfig.headerName -> token)
    }
  }
}
