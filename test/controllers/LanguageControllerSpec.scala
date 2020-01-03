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

package controllers

import play.api.Play
import play.api.http.Status
import play.api.mvc.Cookie
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageControllerSpec extends ControllerBaseSpec {

  val controller = new LanguageController(mockAppConfig, messages)

  "Calling the .switchToLanguage action" when {

    "providing the parameter 'english'" should {

      val result = controller.switchToLanguage("english")(fakeRequest)

      "return a Redirect status (303)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "use the English language" in {
        cookies(result).get(Play.langCookieName(messages)) shouldBe
          Some(Cookie("PLAY_LANG", "en", None, "/", None, secure = false, httpOnly = true))
      }
    }

    "providing the parameter 'cymraeg" should {

      val result = controller.switchToLanguage("cymraeg")(fakeRequest)

      "return a Redirect status (303)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "use the Welsh language" in {
        cookies(result).get(Play.langCookieName(messages)) shouldBe
          Some(Cookie("PLAY_LANG", "cy", None, "/", None, secure = false, httpOnly = true))
      }

    }

    "providing an unsupported language parameter" should {

      controller.switchToLanguage("english")(FakeRequest())
      lazy val result = controller.switchToLanguage("fakeLanguage")(fakeRequest)

      "return a Redirect status (303)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "keep the current language" in {
        cookies(result).get(Play.langCookieName(messages)) shouldBe
          Some(Cookie("PLAY_LANG", "en", None, "/", None, secure = false, httpOnly = true))
      }
    }
  }

  "Calling .langToCall" should {

    val result = controller.langToCall("en")

    "return the correct app config route with language supplied as parameter" in {
      result shouldBe controllers.routes.LanguageController.switchToLanguage("en")
    }
  }
}
