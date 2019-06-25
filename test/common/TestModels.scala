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

package common

import java.time.LocalDate

import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments, ReturnDebitCharge}
import models.{Address, CustomerInformation, MandationStatus}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}

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

  val address: Address = Address("Bedrock Quarry", Some("Bedrock"), Some("Graveldon"), None, Some("GV2 4BB"))
  val entityName: String = "Cheapo Clothing"
  val currentYear: Int = 2018

  val customerInformationMax: CustomerInformation = CustomerInformation(
    Some("Cheapo Clothing Ltd"),
    Some("Betty"),
    Some("Jones"),
    Some(entityName),
    address,
    Some("bettylucknexttime@gmail.com"),
    isHybridUser = false,
    Some("2017-05-05"),
    Some("2017-01-01"),
    Some("7"),
    "10410",
    Some("****1234"),
    Some("69****"),
    Some("MM"),
    Some("MTDfB Voluntary")
  )

  val customerInformationHybrid: CustomerInformation = customerInformationMax.copy(isHybridUser = true)

  val customerInformationMin: CustomerInformation = CustomerInformation(
    None,
    None,
    None,
    None,
    Address("Bedrock Quarry", None, None, None, None),
    None,
    isHybridUser = false,
    None,
    None,
    None,
    "10410",
    None,
    None,
    None,
    None
  )

  val successfulAuthResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.successful(new ~(
    Enrolments(
      Set(
        Enrolment(
          "HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("VRN", "123456789")),
          "Active")
      )
    ),
    Some(Individual)
  ))

  val agentEnrolments: Enrolments = Enrolments(
    Set(
      Enrolment(
        "HMRC-AS-AGENT",
        Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
        "Activated")
    )
  )

  val otherEnrolment: Enrolments = Enrolments(
    Set(
      Enrolment(
        "OTHER-ENROLMENT",
        Seq(EnrolmentIdentifier("BLAH", "12345")),
        "Activated")
    )
  )

  val agentAuthResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.successful(new ~(
    agentEnrolments,
    Some(Agent)
  ))

  val validMandationStatus: MandationStatus = MandationStatus(
    "MTDfB"
  )

  val validNonMTDfBMandationStatus: MandationStatus = MandationStatus(
    "Non MTDfB"
  )
}
