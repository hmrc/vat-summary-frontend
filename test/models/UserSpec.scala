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

package models

import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.play.test.UnitSpec

class UserSpec extends UnitSpec {

  "Creating a User with only a VRN" should {

    val user = User("123456789")

    "have a VRN value the same as the constructor VRN" in {
      user.vrn shouldBe "123456789"
    }

    "have an active status" in {
      user.active shouldBe true
    }
  }

  "Calling a User with a VRN and inactive status" should {

    val user = User("123456789", active = false)

    "have a VRN value the same as the constructor VRN" in {
      user.vrn shouldBe "123456789"
    }

    "have an active status" in {
      user.active shouldBe false
    }
  }

  "containsNonMtdVat" when {

    "user has VATDEC and VATVAR enrolments" should {

      val enrolments = Set(
        Enrolment(
          "HMCE-VATDEC-ORG",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        ),
        Enrolment(
          "HMCE-VATVAR-ORG",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        )
      )

      "return true" in {
        User.containsNonMtdVat(enrolments) shouldBe true
      }
    }

    "user has no VATDEC or VATVAR enrolments" should {

      val enrolments = Set(
        Enrolment(
          "OTHER-ENROLMENT",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        ),
        Enrolment(
          "HMCE-VATVARRRRRRR-ORG",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        )
      )

      "return false" in {
        User.containsNonMtdVat(enrolments) shouldBe false
      }
    }

    "user has one 'old VAT' enrolment" should {

      val enrolments = Set(
        Enrolment(
          "HMCE-VATDEC-ORG",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        )
      )

      "return true" in {
        User.containsNonMtdVat(enrolments) shouldBe true
      }
    }
  }

  "extractVatEnrolments" should {

    val enrolments = Enrolments(
      Set(
        Enrolment(
          "HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("VRN", "123456789")),
          "Activated"
        ),
        Enrolment(
          "HMRC-MTD-IT",
          Seq(EnrolmentIdentifier("SAUTR", "123456789")),
          "Activated"
        ),
        Enrolment(
          "HMCE-VATVAR-ORG",
          Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
          "Activated"
        )
      )
    )

    val expected = Set(
      Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        "Activated"
      ),
      Enrolment(
        "HMCE-VATVAR-ORG",
        Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
        "Activated"
      )
    )

    "extract only VAT enrolments" in {
      User.extractVatEnrolments(enrolments) shouldBe expected
    }
  }
}
