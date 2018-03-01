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

  "Creating a User with a valid, active VAT Enrolment" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-MTD-VAT",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        "Activated"
      ))
    )

    val user = User(enrolments)

    "have the VRN specified in the VAT Enrolment" in {
      user.vrn shouldBe "123456789"
    }

    "have an active status" in {
      user.active shouldBe true
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

    val user = User(enrolments)

    "have the VRN specified in the VAT Enrolment" in {
      user.vrn shouldBe "123456789"
    }

    "have an inactive status" in {
      user.active shouldBe false
    }
  }

  "Creating a User with an invalid VAT Enrolment Key" should {

    val enrolments = Enrolments(
      Set(Enrolment(
        "HMRC-XXX-XXX",
        Seq(EnrolmentIdentifier("VRN", "123456789")),
        ""
      ))
    )

    "throw an exception" in {
      intercept[AuthorisationException] {
        User(enrolments)
      }
    }

    "have the correct message in the exception" in {
      the[AuthorisationException] thrownBy {
        User(enrolments)
      } should have message "VAT enrolment missing"
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
        User(enrolments)
      }
    }

    "have the correct message in the exception" in {
      the[AuthorisationException] thrownBy {
        User(enrolments)
      } should have message "VAT enrolment missing"
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
        User(enrolments)
      }
    }

    "have the correct message in the exception" in {
      the[AuthorisationException] thrownBy {
        User(enrolments)
      } should have message "VRN is invalid"
    }
  }
}
