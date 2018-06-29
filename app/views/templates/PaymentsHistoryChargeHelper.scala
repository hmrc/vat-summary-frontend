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

import common.FinancialTransactionsConstants

sealed case class PaymentsHistoryChargeHelper(name: String, description: String, id: String)

object PaymentsHistoryChargeHelper {
  object VatReturnCreditCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatReturnCreditCharge,
    "paymentsHistory.vatReturnCreditChargeDescription", "credit-charge")

  object VatReturnDebitCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.vatReturnDebitCharge,
    "paymentsHistory.vatReturnDebitChargeDescription", "")

  object VatOfficerAssessmentCreditCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.officerAssessmentCreditCharge,
    "paymentsHistory.officerAssessmentCreditChargeDescription", "")

  object VatOfficerAssessmentDebitCharge extends PaymentsHistoryChargeHelper(
    FinancialTransactionsConstants.officerAssessmentDebitCharge,
    "paymentsHistory.officerAssessmentDebitChargeDescription", "")

  val values = Seq(VatReturnDebitCharge, VatReturnCreditCharge, VatOfficerAssessmentCreditCharge, VatOfficerAssessmentDebitCharge)

  def getChargeType(lookupName: String): Option[PaymentsHistoryChargeHelper] = {
    values.find(_.name == lookupName)
  }
}
