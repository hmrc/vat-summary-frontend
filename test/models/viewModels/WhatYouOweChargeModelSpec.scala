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
import models.User
import models.viewModels.WhatYouOweChargeModel.form
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError
import views.ViewBaseSpec

class WhatYouOweChargeModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "Binding the form" when {

    "all values are valid" when {

      "all fields are present" should {

        val formWithValues = form.mapping.bind(
          Map(
          "chargeValue" -> "Charge Value",
          "chargeDescription" -> "Charge Description",
          "chargeTitle" -> "Charge Title",
          "outstandingAmount" -> "1234.56",
          "originalAmount" -> "2345.67",
          "clearedAmount" -> "1111.11",
          "dueDate" -> "2021-04-08",
          "periodKey" -> "Period Key",
          "isOverdue" -> "true",
          "chargeReference" -> "Charge Reference",
          "makePaymentRedirect" -> "payment-redirect",
          "periodFrom" -> "2021-01-01",
          "periodTo" -> "2021-03-31"
        ))

        "produce a WhatYouOweChargeModel" in {

          formWithValues shouldBe Right(WhatYouOweChargeModel(
            chargeValue = "Charge Value",
            chargeDescription = "Charge Description",
            chargeTitle = "Charge Title",
            outstandingAmount = BigDecimal(1234.56),
            originalAmount = BigDecimal(2345.67),
            clearedAmount = Some(BigDecimal(1111.11)),
            dueDate = LocalDate.parse("2021-04-08"),
            periodKey = Some("Period Key"),
            isOverdue = true,
            chargeReference = Some("Charge Reference"),
            makePaymentRedirect = "payment-redirect",
            periodFrom = Some(LocalDate.parse("2021-01-01")),
            periodTo = Some(LocalDate.parse("2021-03-31"))
          ))
        }

      }

      "only required fields are present" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeValue" -> "Charge Value",
            "chargeDescription" -> "Charge Description",
            "chargeTitle" -> "Charge Title",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true",
            "makePaymentRedirect" -> "payment-redirect"
          ))

        "produce a WhatYouOweChargeModel" in {
          formWithValues shouldBe Right(WhatYouOweChargeModel(
            chargeValue = "Charge Value",
            chargeDescription = "Charge Description",
            chargeTitle = "Charge Title",
            outstandingAmount = BigDecimal(1234.56),
            originalAmount = BigDecimal(1234.56),
            clearedAmount = None,
            dueDate = LocalDate.parse("2021-04-08"),
            periodKey = None,
            isOverdue = true,
            chargeReference = None,
            makePaymentRedirect = "payment-redirect",
            periodFrom = None,
            periodTo = None
          ))
        }
      }
    }

    "there are invalid values"  when {

      "the charge description is missing" should {

        val formWithValues = form.mapping.bind(
          Map(
            "chargeValue" -> "Charge Value",
            "chargeTitle" -> "Charge Title",
            "outstandingAmount" -> "1234.56",
            "originalAmount" -> "1234.56",
            "dueDate" -> "2021-04-08",
            "isOverdue" -> "true",
            "makePaymentRedirect" -> "payment-redirect"
          ))

        "return a form error" in {
          formWithValues shouldBe Left(List(FormError("chargeDescription", List("error.required"),List())))
        }

        "the charge title is missing" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "1234.56",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("chargeTitle", List("error.required"), List())))
          }
        }

        "the charge value is missing" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeTitle" -> "Charge Title",
              "chargeDescription" -> "Charge Description",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "1234.56",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("chargeValue", List("error.required"), List())))
          }
        }

        "the outstanding amount is invalid" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "abcd",
              "originalAmount" -> "1234.56",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("outstandingAmount", List("error.real"), List())))
          }
        }

        "the original amount is invalid" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "abcd",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("originalAmount", List("error.real"), List())))
          }
        }

        "the cleared amount is invalid" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "2345.67",
              "clearedAmount" -> "abcd",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("clearedAmount", List("error.real"), List())))
          }
        }

        "the due date is invalid" should {

          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "2345.67",
              "dueDate" -> "1234",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("dueDate", List("error.date"), List())))
          }
        }

        "the isOverdue value is invalid" should {
          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "2345.67",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "random",
              "makePaymentRedirect" -> "payment-redirect"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("isOverdue", List("error.boolean"), List())))
          }
        }

        "the makePaymentRedirect entry is missing" should {
          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "2345.67",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true"
            ))

          "return a form error" in {
            formWithValues shouldBe Left(List(FormError("makePaymentRedirect", List("error.required"), List())))
          }
        }

        "the period from is invalid" should {
          val formWithValues = form.mapping.bind(
            Map(
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "1234.56",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect",
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
              "chargeValue" -> "Charge Value",
              "chargeDescription" -> "Charge Description",
              "chargeTitle" -> "Charge Title",
              "outstandingAmount" -> "1234.56",
              "originalAmount" -> "1234.56",
              "dueDate" -> "2021-04-08",
              "isOverdue" -> "true",
              "makePaymentRedirect" -> "payment-redirect",
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
  }

  "makePaymentRedirect" when {

    "passed a PaymentWithPeriod" should {

      "return the correct redirect link" in {
        WhatYouOweChargeModel.makePaymentRedirect(payment) shouldBe redirectLinkWithPeriod
      }
    }

    "passed a PaymentNoPeriod" should {

      "return the correct redirect link" in {
        WhatYouOweChargeModel.makePaymentRedirect(paymentOnAccount) shouldBe redirectLinkNoPeriod
      }
    }
  }

  "periodFrom" when {

    "passed a PaymentWithPeriod" should {

      "return its periodFrom field" in {
        WhatYouOweChargeModel.periodFrom(payment) shouldBe Some(LocalDate.parse("2019-01-01"))
      }
    }

    "passed a PaymentNoPeriod" should {

      "return None" in {
        WhatYouOweChargeModel.periodFrom(paymentOnAccount) shouldBe None
      }
    }
  }

  "periodTo" when {

    "passed a PaymentWithPeriod" should {

      "return the periodFrom field" in {
        WhatYouOweChargeModel.periodTo(payment) shouldBe Some(LocalDate.parse("2019-02-02"))
      }
    }

    "passed a PaymentNoPeriod" should {

      "return None" in {
        WhatYouOweChargeModel.periodTo(paymentOnAccount) shouldBe None
      }
    }
  }

  "description()" when {

    "passed a PaymentWithPeriod" when {

      "the user is an agent" should {

        val agentUser: User = User(vrn = "111111111", arn = Some("111111111"))

        "return the correct description message" in {
          WhatYouOweChargeModel.description(paymentWithDifferentAgentMessage, agentUser.isAgent) shouldBe
            Some("because your client should have been registered for VAT earlier")
        }
      }

      "the user is not an agent" should {

        val user: User = User(vrn = "111111111")

        "return the correct message" in {
          WhatYouOweChargeModel.description(paymentWithDifferentAgentMessage, user.isAgent) shouldBe
            Some("because you should have been registered for VAT earlier")
        }
      }

    }

    "passed a PaymentNoPeriod" when {

      "the payment description contains date information" should {

        "return None" in {
          WhatYouOweChargeModel.description(defaultInterestPaymentNoPeriod, userIsAgent = false) shouldBe None
        }
      }

      "the payment description does not contain date information" should {

        "return the correct description message" in {
          WhatYouOweChargeModel.description(paymentNoPeriodNoDate, userIsAgent = false) shouldBe
            Some("charged on the officer’s assessment")
        }

      }
    }
  }
}
