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

package audit

import java.time.LocalDate

import _root_.models.User
import _root_.models.obligations.{VatReturnObligation, VatReturnObligations}
import audit.models.ViewNextOpenVatObligationAuditModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ViewNextOpenVatObligationAuditModelSpec extends AnyWordSpecLike with Matchers {

  val obligationOne = VatReturnObligation(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    LocalDate.parse("2018-03-03"),
    "O",
    None,
    "18AA"
  )

  val user = User("999999999")

  "ViewNextOpenVatObligationAuditModel" should {

    "be constructed correctly when there is one open obligation" in {
      val testData = ViewNextOpenVatObligationAuditModel(
        user,
        obligations = Some(VatReturnObligations(Seq(obligationOne)))
      )

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "obligationOpen" -> "yes",
        "obligationDueBy" -> "2018-03-03",
        "obligationPeriodFrom" -> "2018-01-01",
        "obligationPeriodTo" -> "2018-02-02"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when there are multiple open obligations" in {
      val multipleObligations = Some(VatReturnObligations(Seq(
        obligationOne,
        VatReturnObligation(
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          LocalDate.parse("2019-03-03"),
          "O",
          None,
          "19AA"
        )
      )))

      val testData = ViewNextOpenVatObligationAuditModel(
        user,
        multipleObligations
      )

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "numberOfObligations" -> "2"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when there are no open obligations" in {
      val testData = ViewNextOpenVatObligationAuditModel(
        user,
        None
      )

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "obligationOpen" -> "no"
      )

      testData.detail shouldBe expected
    }
  }
}
