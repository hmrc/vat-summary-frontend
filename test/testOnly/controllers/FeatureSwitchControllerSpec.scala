/*
 * Copyright 2019 HM Revenue & Customs
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

package testOnly.controllers

import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.test.Helpers._

class FeatureSwitchControllerSpec extends ControllerBaseSpec {

  private lazy val target = new FeatureSwitchController(messages, mockAppConfig)

  "Calling the .featureSwitch action" should {

    lazy val result = target.featureSwitch(fakeRequest.addToken())

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      contentType(result) shouldBe Some("text/html")
    }

    "return charset of utf-8" in {
      charset(result) shouldBe Some("utf-8")
    }
  }

  "Calling the .submitFeatureSwitch action" should {

    lazy val result = target.submitFeatureSwitch(fakeRequest.addToken())

    "return 303" in {
      status(result) shouldBe Status.SEE_OTHER
    }

    "redirect the user to the feature switch page" in {
      redirectLocation(result) shouldBe Some(routes.FeatureSwitchController.featureSwitch().url)
    }
  }
}
