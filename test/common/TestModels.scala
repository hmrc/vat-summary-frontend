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

package common

import java.time.LocalDate

import models.{Address, CustomerInformation}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments, ReturnDebitCharge}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}

import scala.concurrent.Future

object TestModels {

  val payments: Payments = Payments(Seq(Payment(
    ReturnDebitCharge,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    LocalDate.parse("2019-03-03"),
    1,
    Some("#001")
  )))

  val obligations: VatReturnObligations = VatReturnObligations(Seq(VatReturnObligation(
    LocalDate.parse("2019-04-04"),
    LocalDate.parse("2019-05-05"),
    LocalDate.parse("2019-06-06"),
    "O",
    None,
    "#001"
  )))

  val overdueObligations: VatReturnObligations = VatReturnObligations(Seq(VatReturnObligation(
    LocalDate.parse("2017-04-04"),
    LocalDate.parse("2017-05-05"),
    LocalDate.parse("2017-06-06"),
    "O",
    None,
    "#001"
  )))

  val overduePayment: Payments = Payments(Seq(Payment(
    ReturnDebitCharge,
    LocalDate.parse("2017-01-01"),
    LocalDate.parse("2017-02-02"),
    LocalDate.parse("2017-03-03"),
    1,
    Some("#001")
  )))

  val address: Address = Address("", "", None, None, None)
  val entityName: String = "Cheapo Clothing"
  val currentYear: Int = 2018

  val customerInformation: CustomerInformation = CustomerInformation(
    Some("Cheapo Clothing Ltd"),
    Some("Betty"),
    Some("Jones"),
    Some(entityName),
    address,
    None,
    None,
    None,
    address,
    None,
    None,
    None,
    isHybridUser = false
  )

  val customerInformationHybrid: CustomerInformation = CustomerInformation(
    Some("Cheapo Clothing Ltd"),
    Some("Betty"),
    Some("Jones"),
    Some(entityName),
    address,
    None,
    None,
    None,
    address,
    None,
    None,
    None,
    isHybridUser = true
  )

  val customerInformationNoEntityName: CustomerInformation = CustomerInformation(
    None,
    None,
    None,
    None,
    address,
    None,
    None,
    None,
    address,
    None,
    None,
    None,
    isHybridUser = false
  )

  val successfulAuthResult: Future[Enrolments] = Future.successful(Enrolments(Set(
      Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
  )))
}
