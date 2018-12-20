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

package views.templates

import models.payments._

sealed case class PaymentsHistoryChargeHelper(name: String, title: String, description: String, id: String = "")

object PaymentsHistoryChargeHelper {
  object VatReturnCreditCharge extends PaymentsHistoryChargeHelper(
    ReturnCreditCharge.value,
    "paymentsHistory.vatReturnCreditChargeTitle",
    "paymentsHistory.vatReturnCreditChargeDescription")

  object VatReturnDebitCharge extends PaymentsHistoryChargeHelper(
    ReturnDebitCharge.value,
    "paymentsHistory.vatReturnDebitChargeTitle",
    "paymentsHistory.vatReturnDebitChargeDescription")

  object VatOfficerAssessmentCreditCharge extends PaymentsHistoryChargeHelper(
    OACreditCharge.value,
    "paymentsHistory.officerAssessmentChargeTitle",
    "paymentsHistory.officerAssessmentCreditChargeDescription")

  object VatOfficerAssessmentDebitCharge extends PaymentsHistoryChargeHelper(
    OADebitCharge.value,
    "paymentsHistory.officerAssessmentChargeTitle",
    "paymentsHistory.officerAssessmentDebitChargeDescription")

  object VatCentralAssessment extends PaymentsHistoryChargeHelper(
    CentralAssessmentCharge.value,
    "paymentsHistory.vatCentralAssessmentTitle",
    "paymentsHistory.vatCentralAssessmentDescription")

  object VatDefaultSurcharge extends PaymentsHistoryChargeHelper(
    DefaultSurcharge.value,
    "paymentsHistory.vatDefaultSurchargeTitle",
    "paymentsHistory.vatDefaultSurchargeDescription")

  object VatErrorCorrectionDebitCharge extends PaymentsHistoryChargeHelper(
    ErrorCorrectionDebitCharge.value,
    "paymentsHistory.vatErrorCorrectionDebitChargeTitle",
    "paymentsHistory.vatErrorCorrectionChargeDescription"
  )

  object VatErrorCorrectionCreditCharge extends PaymentsHistoryChargeHelper(
    ErrorCorrectionCreditCharge.value,
    "paymentsHistory.vatErrorCorrectionCreditChargeTitle",
    "paymentsHistory.vatErrorCorrectionChargeDescription"
  )

  object VatRepaymentSupplement extends PaymentsHistoryChargeHelper(
    RepaymentSupplement.value,
    "paymentsHistory.vatRepaymentSupplementTitle",
    "paymentsHistory.vatRepaymentSupplementDescription",
    "repayment"
  )

  object OADefaultInterest extends PaymentsHistoryChargeHelper(
    OADefaultInterestCharge.value,
    "paymentsHistory.OADefaultInterestTitle",
    "paymentsHistory.OADefaultInterestDescription"
  )

  val values = Seq(VatReturnDebitCharge, VatReturnCreditCharge, VatOfficerAssessmentCreditCharge,
    VatOfficerAssessmentDebitCharge, VatCentralAssessment, VatDefaultSurcharge,
    VatErrorCorrectionDebitCharge, VatErrorCorrectionCreditCharge, VatRepaymentSupplement, OADefaultInterest)

  def getChargeType(lookupName: String): Option[PaymentsHistoryChargeHelper] = {
    values.find(_.name == lookupName)
  }
}
