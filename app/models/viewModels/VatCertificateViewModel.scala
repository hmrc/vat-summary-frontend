/*
 * Copyright 2023 HM Revenue & Customs
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

import models.{Address, CustomerInformation, TaxPeriod}

case class VatCertificateViewModel(
                                    vrn: String,
                                    registrationDate: Option[LocalDate],
                                    certificateDate: LocalDate,
                                    businessName: Option[String],
                                    tradingName: Option[String],
                                    businessTypeMsgKey: String,
                                    tradeClassification: Option[String],
                                    ppob: Address,
                                    returnPeriodMsgKey: String,
                                    nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                                    firstNonNSTPPeriod: Option[TaxPeriod],
                                    fullName: Option[String]
                                  )

object VatCertificateViewModel {
  def fromCustomerInformation(vrn: String, customerInformation: CustomerInformation): VatCertificateViewModel = {
    val customerFullName: Option[String] = (customerInformation.details.firstName, customerInformation.details.lastName) match {
      case (Some(firstName), Some(secondName)) => Some(s"$firstName $secondName")
      case _ => None
    }

    VatCertificateViewModel(
      vrn, customerInformation.details.vatRegistrationDate.map(LocalDate.parse(_)), LocalDate.now(),
      customerInformation.details.organisationName, customerInformation.details.tradingName,
      customerInformation.partyTypeMessageKey, customerInformation.sicCode,
      customerInformation.businessAddress, customerInformation.returnPeriodMessageKey,
      customerInformation.nonStdTaxPeriods, customerInformation.firstNonNSTPPeriod,
      customerFullName
    )
  }
}
