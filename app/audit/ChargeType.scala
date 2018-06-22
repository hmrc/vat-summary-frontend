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

package audit 

sealed case class ChargeType(name: String, description: String, id: String)

object ChargeType {
  object VAT_RETURN_CREDIT_CHARGE extends ChargeType("VAT Return Credit Charge", "paymentsHistory.vatReturnCreditChargeDescription", "credit-charge")
  object VAT_RETURN_DEBIT_CHARGE extends ChargeType("VAT Return Debit Charge", "paymentsHistory.vatReturnCreditChargeDescription", "debit-charge")

  val values = Seq(VAT_RETURN_CREDIT_CHARGE, VAT_RETURN_DEBIT_CHARGE)

  def getChargeType(lookupName: String): Option[ChargeType] = {
    values.find(_.name == lookupName)
  }
}
