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

package app

import uk.gov.hmrc.play.test.UnitSpec

class RouteSpec extends UnitSpec {

  "The route for the BTA partial" should {
    "be /bta-home" in {
      controllers.partials.routes.BtaHomeController.vatSection().url shouldBe "/bta-home"
    }
  }

  "The route for the BTA stub" should {
    "be /bta-stub" in {
      controllers.routes.BtaStubController.landingPage().url shouldBe "/bta-stub"
    }
  }

}
