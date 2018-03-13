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

import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerBaseSpec {

  private trait SignOutControllerTest {
    def target: SignOutController = {
      new SignOutController(messages, mockAppConfig)
    }
  }

  "navigating to signout page" should {
    "return 303 and navigate to the survey url" in new SignOutControllerTest {
      lazy val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequestWithSession
      lazy val result: Future[Result] = target.signOut()(request)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(mockAppConfig.signOutUrl)
    }

  }

}