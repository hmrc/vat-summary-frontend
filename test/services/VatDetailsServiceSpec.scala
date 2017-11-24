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

package services

import java.time.LocalDate
import controllers.ControllerBaseSpec
import models.{Obligation, Obligations}

class VatDetailsServiceSpec extends ControllerBaseSpec {

  "Calling VatDetailsService .retrieveNextReturnObligation" when {

    val currentObligation: Obligation = Obligation(
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-30"),
      due = LocalDate.parse("2017-04-30"),
      "O",
      None,
      "#001"
    )

    "sequence contains one obligation" should {

      val obligations = Obligations(Seq(currentObligation))
      lazy val service = new VatDetailsService
      lazy val result = service.retrieveNextReturnObligation(obligations)

      "return current obligation" in {
        result shouldBe currentObligation
      }
    }

    "sequence contains more than one obligation" should {

      val futureObligation: Obligation = Obligation(
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-03-30"),
        due = LocalDate.parse("2017-07-30"),
        "O",
        None,
        "#001"
      )

      val obligations = Obligations(Seq(futureObligation, currentObligation))
      lazy val service = new VatDetailsService
      lazy val result = service.retrieveNextReturnObligation(obligations)

      "return the current obligation" in {
        result shouldBe currentObligation
      }
    }
  }
}
