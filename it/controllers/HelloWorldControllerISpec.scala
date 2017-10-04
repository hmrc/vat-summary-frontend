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

package controllers

import helpers.BaseIntegrationSpec
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.UnitSpec
import scala.concurrent.Future

class HelloWorldControllerISpec extends UnitSpec with BaseIntegrationSpec {

  val controller: HelloWorldController = app.injector.instanceOf[HelloWorldController]

  "Calling the helloWorld action" when {

    "user is authenticated" should {
      "return 200" in {
        given.user.isAuthenticated
        val result: Future[Result] = controller.helloWorld(FakeRequest())
        await(result).header.status shouldBe 200
      }
    }

    "user is not authenticated" should {
      "return 303" in {
        given.user.isNotAuthenticated
        val result: Future[Result] = controller.helloWorld(FakeRequest())
        await(result).header.status shouldBe 303
      }
    }
  }
}
