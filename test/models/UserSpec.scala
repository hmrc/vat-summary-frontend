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

  "A User with valid MTD-VAT, VATDEC and VATVAR enrolments" should {

    val enrolments = Enrolments(
      Set(
        Enrolment(
          "HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("VRN", "123456789")),
          "Activated"
        ),
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
    )

    val user = User(enrolments, None)

    "say that it has the VATDEC and VATVAR enrolments" in {
      user.hasNonMtdVat shouldBe true
    }

  }

  "A User with only a valid MTD-VAT enrolment" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        "Activated"
      ))
    )

    val user = User(enrolments, None)

    "say that it doesn't have the VATDEC and VATVAR enrolments" in {
      user.hasNonMtdVat shouldBe false
    }

    "isAgent should return false" in {
      user.isAgent shouldBe false
    }
  }

  "Creating a User with an Agent Services enrolment" when {

    "a delegated enrolment VRN is supplied" should {

      val enrolments = Enrolments(
        Set(Enrolment(
          "HMRC-AS-AGENT",
          Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
          "Activated"
        ))
      )

      val user = User(enrolments, Some("909090909"))

      "have the VRN specified in the delegated enrolment" in {
        user.vrn shouldBe "909090909"
      }

      "have the ARN specified in the Agent enrolment" in {
        user.arn shouldBe Some("XARN1234567")
      }

      "have the status 'Activated'" in {
        user.active shouldBe true
      }

      "isAgent should return true" in {
        user.isAgent shouldBe true
      }
    }

    "a delegated enrolment VRN is not supplied" should {

      val enrolments = Enrolments(
        Set(Enrolment(
          "HMRC-AS-AGENT",
          Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
          "Activated"
        ))
      )

      "throw an exception" in {

        intercept[AuthorisationException] {
          User(enrolments, None)
        }
      }

      "have the correct message in the exception" in {

        the[AuthorisationException] thrownBy {
          User(enrolments, None)
        } should have message "Delegated enrolment missing"
      }
    }
  }

  "Creating a User with a valid, active VAT Enrolment" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        "Activated"
      ))
    )

    val user = User(enrolments, None)

    "have the VRN specified in the VAT Enrolment" in {
      user.vrn shouldBe "123456789"
    }

    "have an active status" in {
      user.active shouldBe true
    }

    "isAgent should return false" in {
      user.isAgent shouldBe false
    }
  }

  "Creating a User with a valid, inactive VAT Enrolment" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        ""
      ))
    )

    val user = User(enrolments, None)

    "have the VRN specified in the VAT Enrolment" in {
      user.vrn shouldBe "123456789"
    }

    "have an inactive status" in {
      user.active shouldBe false
    }

    "isAgent should return false" in {
      user.isAgent shouldBe false
    }
  }

  "Creating a User with an invalid VAT Identifier Name" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VATXXXXX", "123456789")),
        ""
      ))
    )

    "throw an exception" in {
      intercept[AuthorisationException] {
        User(enrolments, None)
      }
    }

    "have the correct message in the exception" in {
      the[AuthorisationException] thrownBy {
        User(enrolments, None)
      } should have message "VAT identifier invalid"
    }
  }

  "Creating a User with an invalid VRN" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "")),
        ""
      ))
    )

    "throw an exception" in {
      intercept[AuthorisationException] {
        User(enrolments, None)
      }
    }

    "have the correct message in the exception" in {
      the[AuthorisationException] thrownBy {
        User(enrolments, None)
      } should have message "VRN is invalid"
    }
  }
}
