/*
 * Copyright 2022 HM Revenue & Customs
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

package models.viewModels

import java.time.LocalDate

import common.TestModels._
import models.viewModels.StandardChargeViewModel.form
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError
import views.ViewBaseSpec

class StandardChargeViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "Binding the form" when {

    "all values are valid" when {

      "all fields are present" should {

        val formWithValues = form.mapping.bind(
          Map(
          "chargeType" -> "Charge Value",
          "outstandingAmount" -> "1234.56",
          "originalAmount" -> "2345.67",
          "clearedAmount" -> "1111.11",
          "dueDate" -> "2021-04-08",
          "periodKey" -> "Period Key",
          "isOverdue" -> "true",
          "chargeReference" -> "Charge Reference",
          "periodFrom" -> "2021-01-01",
          "periodTo" -> "2021-03-31"
        ))

        "produce a WhatYouOweChargeModel" in {

          formWithValues shouldBe Right(StandardChargeViewModel(
            chargeType = "Charge Value",
            outstandingAmount = BigDecimal(1234.56),
            originalAmount = BigDecimal(2345.67),
            clearedAmount = Some(BigDecimal(1111.11)),
            dueDate = LocalDate.parse("2021-04-08"),
            periodKey = Some("Period Key"),
            isOverdue = true,
            chargeReference = Some("Charge Reference"),
            periodFrom = Some(LocalDate.parse("2021-01-01")),
            periodTo = Some(LocalDate.parse("2021-03-31"))
          ))
        }

      }

      "only required fields are present" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true"
          ))

        "produce a WhatYouOweChargeModel" in {
          formWithValues shouldBe Right(StandardChargeViewModel(
            chargeType = "Charge Value",
            outstandingAmount = BigDecimal(1234.56),
            originalAmount = BigDecimal(1234.56),
            clearedAmount = None,
            dueDate = LocalDate.parse("2021-04-08"),
            periodKey = None,
            isOverdue = true,
            chargeReference = None,
            periodFrom = None,
            periodTo = None
          ))
        }
      }
    }

    "there are invalid values"  when {

      "the charge title is missing" should {

        val formWithValues = form.mapping.bind(
          Map(
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("chargeType", List("error.required"), List())))
        }
      }

      "the outstanding amount is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "abcd",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("outstandingAmount", List("error.real"), List())))
        }
      }

      "the original amount is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "abcd",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("originalAmount", List("error.real"), List())))
        }
      }

      "the cleared amount is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "2345.67",
            "clearedAmount" -> "abcd",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("clearedAmount", List("error.real"), List())))
        }
      }

      "the due date is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "2345.67",
            "dueDate" -> "1234",
            "isOverdue" -> "true"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("dueDate", List("error.date"), List())))
        }
      }

      "the isOverdue value is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "2345.67",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "random"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("isOverdue", List("error.boolean"), List())))
        }
      }

      "the period from is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true",
            "periodFrom" -> "true",
            "periodTo" -> "2021-03-31"
          )
        )

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("periodFrom", List("error.date"), List())))
        }
      }

      "the period to is invalid" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeType" -> "Charge Value",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true",
            "periodFrom" -> "2021-01-01",
            "periodTo" -> "false"
          )
        )

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("periodTo", List("error.date"), List())))
        }
      }
    }
  }

  val model: StandardChargeViewModel = StandardChargeViewModel(
    chargeType = "VAT Inaccuracy return replaced",
    outstandingAmount = BigDecimal(1234.56),
    originalAmount = BigDecimal(2345.67),
    clearedAmount = Some(BigDecimal(1111.11)),
    dueDate = LocalDate.parse("2021-04-08"),
    periodKey = Some("Period Key"),
    isOverdue = true,
    chargeReference = Some("XD002750002155"),
    periodFrom = Some(LocalDate.parse("2021-01-01")),
    periodTo = Some(LocalDate.parse("2021-03-31"))
  )

  val noDatesModel: StandardChargeViewModel = model.copy(periodFrom = None, periodTo = None)

  "makePaymentRedirect" when {

    "the charge has a period To date" should {

      "return the correct redirect link" in {
        model.makePaymentRedirect shouldBe "/vat-through-software/make-payment/123456" +
          "/3/2021/2021-03-31/VAT%20Inaccuracy%20return%20replaced/2021-04-08/XD002750002155"
      }
    }

    "the charge has no period dates" should {

      "return the correct redirect link" in {
        noDatesModel.makePaymentRedirect shouldBe "/vat-through-software/make-payment/123456" +
          "/VAT%20Inaccuracy%20return%20replaced/2021-04-08/XD002750002155"
      }
    }
  }

  "periodFrom" when {

    "passed a PaymentWithPeriod" should {

      "return its periodFrom field" in {
        StandardChargeViewModel.periodFrom(payment) shouldBe Some(LocalDate.parse("2019-01-01"))
      }
    }

    "passed a PaymentNoPeriod" should {

      "return None" in {
        StandardChargeViewModel.periodFrom(paymentOnAccount) shouldBe None
      }
    }
  }

  "periodTo" when {

    "passed a PaymentWithPeriod" should {

      "return the periodFrom field" in {
        StandardChargeViewModel.periodTo(payment) shouldBe Some(LocalDate.parse("2019-02-02"))
      }
    }

    "passed a PaymentNoPeriod" should {

      "return None" in {
        StandardChargeViewModel.periodTo(paymentOnAccount) shouldBe None
      }
    }
  }

  "description()" when {

    "the charge has period from and to dates" when {

      "the user is an agent" should {

        "return the correct description message" in {
          model.description(isAgent = true) shouldBe
            "because your client submitted inaccurate information for the period 1 Jan to 31 Mar 2021"
        }
      }

      "the user is not an agent" should {

        "return the correct message" in {
          model.description(isAgent = false) shouldBe
            "because you have submitted inaccurate information for the period 1 Jan to 31 Mar 2021"
        }
      }

    }

    "the charge has no period dates" when {

      "the payment description contains date information" should {

        "return None" in {
          noDatesModel.description(isAgent = false) shouldBe ""
        }
      }

      "the payment description does not contain date information" should {

        val noDatesModel = model.copy(chargeType = "VAT Unrepayable Overpayment", periodFrom = None, periodTo = None)

        "return the correct description message" in {
          noDatesModel.description(isAgent = false) shouldBe "cannot be repaid after 4 years"
        }
      }
    }
  }
}
