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

package models.ViewModels

import common.TestModels.{customerInformationMax, customerInformationMin, vatCertificateViewModelMax, vatCertificateViewModelMin}
import models.viewModels.VatCertificateViewModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class VatCertificateViewModelSpec extends AnyWordSpecLike with Matchers {

  "The VatCertificateViewModel .fromCustomerInformation()" when {

    lazy val model = VatCertificateViewModel

    "the customer has full information" should {

      "return a view model with the correct fields" in {
        model.fromCustomerInformation("999999999", customerInformationMax) shouldBe vatCertificateViewModelMax
      }
    }

    "the customer is missing a first or last name" should {

      "return a view model with a customer full name of None" in {
        model.fromCustomerInformation("999999999", customerInformationMin) shouldBe vatCertificateViewModelMin
      }
    }
  }
}
