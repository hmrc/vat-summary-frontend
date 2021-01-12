/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class RouteSpec extends UnitSpec with GuiceOneAppPerSuite {

  "The route for the BTA partial" should {
    "be /vat-summary-partials/bta-home" in {
      controllers.partials.routes.BtaHomeController.vatSection().url shouldBe "/vat-summary-partials/bta-home"
    }
  }

  "The route for the partialMigration partial" should {
    "be /vat-summary-partials/bta-home" in {
      controllers.partials.routes.BtaHomeController.partialMigration().url shouldBe "/vat-summary-partials/partial-migration"
    }
  }

  "The route for the VAT details controller" should {
    "be /vat-through-software/vat-overview" in {
      controllers.routes.VatDetailsController.details().url shouldBe "/vat-through-software/vat-overview"
    }
  }

  "The route for the Open payments controller" should {
    "be /vat-through-software/what-you-owe" in {
      controllers.routes.OpenPaymentsController.openPayments().url shouldBe "/vat-through-software/what-you-owe"
    }
  }

  "The route for the Payment History controller" should {
    "be /vat-through-software/payment-history/{year}" in {
      controllers.routes.PaymentHistoryController.paymentHistory().url shouldBe "/vat-through-software/payment-history"
    }
  }
}
