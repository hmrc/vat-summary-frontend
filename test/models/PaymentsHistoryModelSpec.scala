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

import java.time.LocalDate

import models.viewModels.PaymentsHistoryModel
import play.api.libs.json.{JsSuccess, Json, Reads}
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHistoryModelSpec extends UnitSpec {

  val reads: Reads[Seq[PaymentsHistoryModel]] = PaymentsHistoryModel.reads

  "reads" should {
    "read json containing multiple items per period into a sequence" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-01-01",
          |         "taxPeriodTo" : "2018-02-20",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-01-10",
          |                 "amount" : 100
          |             },
          |             {
          |                 "clearingDate" : "2018-02-15",
          |                 "amount" : 1000
          |             }
          |         ]
          |     },
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-03-01",
          |         "taxPeriodTo" : "2018-04-26",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-03-10",
          |                 "amount" : 10000
          |             },
          |             {
          |                 "clearingDate" : "2018-04-15",
          |                 "amount" : 1000000
          |             }
          |         ]
          |      }
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 100,
          clearedDate   = LocalDate.of(2018, 1, 10)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 1000,
          clearedDate   = LocalDate.of(2018, 2, 15)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 3, 1),
          taxPeriodTo   = LocalDate.of(2018, 4, 26),
          amount        = 10000,
          clearedDate   = LocalDate.of(2018, 3, 10)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 3, 1),
          taxPeriodTo   = LocalDate.of(2018, 4, 26),
          amount        = 1000000,
          clearedDate   = LocalDate.of(2018, 4, 15)
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item per period into a sequence" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-01-01",
          |         "taxPeriodTo" : "2018-02-20",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-01-10",
          |                 "amount" : 100
          |             }
          |         ]
          |     },
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-03-01",
          |         "taxPeriodTo" : "2018-04-26",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-03-10",
          |                 "amount" : 10000
          |             }
          |         ]
          |      }
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 100,
          clearedDate   = LocalDate.of(2018, 1, 10)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 3, 1),
          taxPeriodTo   = LocalDate.of(2018, 4, 26),
          amount        = 10000,
          clearedDate   = LocalDate.of(2018, 3, 10)
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item per period into a sequence with different charge types" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-01-01",
          |         "taxPeriodTo" : "2018-02-20",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-01-10",
          |                 "amount" : 100
          |             }
          |         ]
          |     },
          |     {
          |         "chargeType" : "PAYE",
          |         "taxPeriodFrom" : "2018-03-01",
          |         "taxPeriodTo" : "2018-04-26",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-03-10",
          |                 "amount" : 10000
          |             }
          |         ]
          |      }
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 100,
          clearedDate   = LocalDate.of(2018, 1, 10)
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing multiple items in only one period" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-01-01",
          |         "taxPeriodTo" : "2018-02-20",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-01-10",
          |                 "amount" : 100
          |             },
          |             {
          |                 "clearingDate" : "2018-02-15",
          |                 "amount" : 1000
          |             }
          |         ]
          |     }
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 100,
          clearedDate   = LocalDate.of(2018, 1, 10)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 1000,
          clearedDate   = LocalDate.of(2018, 2, 15)
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item in only one period" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |     {
          |         "chargeType" : "VAT Return charge",
          |         "taxPeriodFrom" : "2018-01-01",
          |         "taxPeriodTo" : "2018-02-20",
          |         "items" : [
          |             {
          |                 "clearingDate" : "2018-01-10",
          |                 "amount" : 100
          |             },
          |             {
          |                 "clearingDate" : "2018-02-15",
          |                 "amount" : 1000
          |             }
          |         ]
          |     }
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 100,
          clearedDate   = LocalDate.of(2018, 1, 10)
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.of(2018, 1, 1),
          taxPeriodTo   = LocalDate.of(2018, 2, 20),
          amount        = 1000,
          clearedDate   = LocalDate.of(2018, 2, 15)
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json into an empty sequence" in {
      val testJson = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq = Seq.empty[PaymentsHistoryModel]

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "throw an IllegalStateException" when {
      "there is no financialTransactions block" in {
        val testJson = Json.parse("""{ "abc" : "xyz" }""")

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe "The data for key financialTransactions could not be found in the Json"
      }

      "there is no items block" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "taxPeriodFrom" : "2018-01-01",
            |             "taxPeriodTo" : "2018-02-20"
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe s"The data for key items could not be found in the Json"
      }

      "the items list is empty" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "taxPeriodFrom" : "2018-01-01",
            |             "taxPeriodTo" : "2018-02-20",
            |             "items" : [
            |
            |             ]
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe "The items list was found but the list was empty"
      }

      "there is no amount" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "taxPeriodFrom" : "2018-01-01",
            |             "taxPeriodTo" : "2018-02-20",
            |             "items" : [
            |               {
            |                   "abc" : "xyz"
            |               }
            |             ]
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe s"The data for key amount could not be found in the Json"
      }

      "there is no clearingDate" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "taxPeriodFrom" : "2018-01-01",
            |             "taxPeriodTo" : "2018-02-20",
            |             "items" : [
            |               {
            |                   "amount" : 1
            |               }
            |             ]
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe s"The data for key clearingDate could not be found in the Json"
      }

      "there is no taxPeriodFrom" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "items" : [
            |               {
            |                   "amount" : 1,
            |                   "clearingDate" : "2018-01-02"
            |               }
            |             ]
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe s"The data for key taxPeriodFrom could not be found in the Json"
      }

      "there is no taxPeriodTo" in {
        val testJson = Json.parse(
          """
            |{
            |   "financialTransactions" : [
            |         {
            |             "chargeType" : "VAT Return charge",
            |             "taxPeriodFrom" : "2018-01-01",
            |             "items" : [
            |               {
            |                   "amount" : 1,
            |                   "clearingDate" : "2018-01-02"
            |               }
            |             ]
            |         }
            |   ]
            |}
          """.stripMargin
        )

        val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe s"The data for key taxPeriodTo could not be found in the Json"
      }
    }
  }
}
