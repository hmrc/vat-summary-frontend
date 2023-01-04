/*
 * Copyright 2023 HM Revenue & Customs
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

package models.penalties

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import common.TestModels._


class LPPDetailsSpec extends AnyWordSpec with Matchers{


  "LPP details" should {

    "parse from JSON" when {

      "optional fields are present" in {
        LPPDetailsJsonMax.as[LPPDetails] shouldBe LPPDetailsModelMax
      }
      "optional are not present" in {
        LPPDetailsJsonMin.as[LPPDetails] shouldBe LPPDetailsModelMin
      }
    }
  }



}
