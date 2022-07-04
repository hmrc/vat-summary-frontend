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

package common

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import java.time.LocalDate

import models._
import models.errors.PenaltiesFeatureSwitchError
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments._
import models.penalties.PenaltiesSummary
import models.viewModels.{VatCertificateViewModel, VatDetailsViewModel, StandardChargeViewModel, WhatYouOweViewModel, CrystallisedInterestViewModel}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}

import scala.concurrent.Future

object TestModels {

  val testDate: LocalDate = LocalDate.parse("2018-05-01")

  val payments: Payments = Payments(Seq(Payment(
    ReturnDebitCharge,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    LocalDate.parse("2019-03-03"),
    1,
    Some("#001"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false
  )))

  val payment: PaymentWithPeriod = Payment(
    ReturnDebitCharge,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    LocalDate.parse("2019-03-03"),
    BigDecimal("10000"),
    Some("ABCD"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    originalAmount = Some(1000.00),
    clearedAmount = Some(00.00)
  )

  val paymentNoPeriodNoDate: PaymentNoPeriod = Payment(
    OADefaultInterestCharge,
    LocalDate.parse("2019-03-03"),
    BigDecimal("10000"),
    Some("ABCD"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    originalAmount = Some(1000.00),
    clearedAmount = Some(00.00)
  )

  val defaultInterestPaymentNoPeriod: PaymentNoPeriod = paymentNoPeriodNoDate.copy(chargeType = VatDefaultInterest)

  val paymentWithDifferentAgentMessage: PaymentWithPeriod = payment.copy(chargeType = BnpRegPost2010Charge)

  val paymentOnAccount: PaymentNoPeriod = Payment(
    PaymentOnAccount,
    LocalDate.parse("2017-01-01"),
    BigDecimal("0"),
    None,
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false
  )

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

  val address: Address = Address("Bedrock Quarry", Some("Bedrock"), Some("Graveldon"), None, Some("GV2 4BB"))
  val entityName: String = "Cheapo Clothing"
  val currentYear: Int = 2018

  val customerDetailsMax: CustomerDetails = CustomerDetails(
    Some("Betty"),
    Some("Jones"),
    Some(entityName),
    Some("Cheapo Clothing Ltd"),
    isInsolvent = false,
    Some(true),
    Some("01"),
    Some("2018-01-01"),
    Some("2017-01-01")
  )

  val customerDetailsMin: CustomerDetails = CustomerDetails(
    None,
    None,
    None,
    None,
    isInsolvent = false,
    None,
    None,
    None,
    None
  )

  val customerDetailsInsolvent: CustomerDetails = customerDetailsMax.copy(isInsolvent = true, continueToTrade = Some(false))
  val customerDetailsInsolventTrading: CustomerDetails = customerDetailsMax.copy(isInsolvent = true)
  val customerDetailsInsolventTradingExempt: CustomerDetails =
    customerDetailsMax.copy(isInsolvent = true, continueToTrade = Some(false), insolvencyType = Some("07"))
  val customerDetailsInsolventFuture: CustomerDetails = customerDetailsMax.copy(
    isInsolvent = true, insolvencyDate = Some("2019-01-01")
  )

  val email = "bettylucknexttime@gmail.com"
  val customerInformationMax: CustomerInformation = CustomerInformation(
    customerDetailsMax,
    address,
    emailAddress = Some(Email(Some(email), Some(true))),
    isHybridUser = false,
    Some("2017-05-05"),
    Some("2017-05-06"),
    Some("7"),
    Some("10410"),
    Some("MM"),
    Some(List(
      TaxPeriod(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-01-15")),
      TaxPeriod(LocalDate.parse("2018-01-06"), LocalDate.parse("2018-01-28")))
    ),
    Some(TaxPeriod(LocalDate.parse("2018-01-29"), LocalDate.parse("2018-01-31"))),
    Some("MTDfB Voluntary"),
    Some(Deregistration(Some(LocalDate.parse("2020-01-01")))),
    Some(ChangeIndicators(false)),
    isMissingTrader = false,
    hasPendingPpobChanges = false,
    mandationStatus = "MTDfB"
  )

  val customerInformationInsolvent: CustomerInformation = customerInformationMax.copy(details = customerDetailsInsolvent)
  val customerInformationInsolventTrading: CustomerInformation =
    customerInformationMax.copy(details = customerDetailsInsolventTrading)
  val customerInformationInsolventTradingExempt: CustomerInformation =
    customerInformationMax.copy(details = customerDetailsInsolventTradingExempt)
  val customerInformationInsolventFuture: CustomerInformation =
    customerInformationMax.copy(details = customerDetailsInsolventFuture)
  val customerInformationLaterMigratedToETMPDate: CustomerInformation =
    customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-04-01"))


  val customerInformationHybrid: CustomerInformation = customerInformationMax.copy(isHybridUser = true)

  val customerInformationMin: CustomerInformation = CustomerInformation(
    customerDetailsMin,
    Address("Bedrock Quarry", None, None, None, None),
    None,
    isHybridUser = false,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    isMissingTrader = false,
    hasPendingPpobChanges = false,
    mandationStatus = "MTDfB"
  )

  val customerInformationMTDfBExempt: CustomerInformation = customerInformationMin.copy(mandationStatus = "MTDfB Exempt")
  val customerInformationNonMTDfB: CustomerInformation = customerInformationMin.copy(mandationStatus = "Non MTDfB")
  val customerInformationJunkStatus: CustomerInformation = customerInformationMin.copy(mandationStatus = "Some Status")

  val vatCertificateViewModelMin: VatCertificateViewModel = VatCertificateViewModel(
    "999999999",
    None,
    LocalDate.now(),
    None,
    None,
    "common.notProvided",
    None,
    Address("Bedrock Quarry", None, None, None, None),
    "common.notProvided",
    None,
    None,
    None
  )

  val vatCertificateViewModelMax: VatCertificateViewModel = VatCertificateViewModel(
    "999999999",
    Some(LocalDate.parse("2017-01-01")),
    LocalDate.now(),
    Some("Cheapo Clothing Ltd"),
    Some("Cheapo Clothing"),
    "partyType.7",
    Some("10410"),
    Address("Bedrock Quarry", Some("Bedrock"), Some("Graveldon"), None, Some("GV2 4BB")),
    "returnPeriod.MM",
    Some(List(
      TaxPeriod("2018-01-01", "2018-01-15"),
      TaxPeriod("2018-01-06", "2018-01-28"),
    )),
    Some(TaxPeriod("2018-01-29", "2018-01-31")),
    Some("Betty Jones")
  )

  val vatDetailsModel: VatDetailsViewModel = VatDetailsViewModel(
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), currentDate = testDate, partyType = Some("1"), mandationStatus = "2"
  )

  val vatDetailsGroupModel: VatDetailsViewModel = VatDetailsViewModel(
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), currentDate = testDate, partyType = Some(PartyTypes.vatGroup), mandationStatus = "2"
  )

  val vatDetailsDeregModel: VatDetailsViewModel = VatDetailsViewModel(
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), deregDate = Some(LocalDate.parse("2020-02-02")), currentDate = testDate, partyType = Some("1"), mandationStatus = "2"
  )

  val vatDetailsHistoricDeregModel: VatDetailsViewModel = VatDetailsViewModel(
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), deregDate = Some(LocalDate.parse("2017-02-02")), currentDate = testDate,
    partyType = Some(PartyTypes.vatGroup), mandationStatus = "2"
  )

  val vatDetailsPendingDeregModel: VatDetailsViewModel = VatDetailsViewModel(
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), pendingDereg = true, currentDate = testDate, partyType = Some("1"), mandationStatus = "2"
  )

  val mtdEnrolment: Enrolment = Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
  val vatDecEnrolment: Enrolment = Enrolment("HMCE-VATDEC-ORG", Seq(EnrolmentIdentifier("VATRegNo", "123456789")), "")
  val successfulAuthResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.successful(new ~(
    Enrolments(Set(mtdEnrolment)),
    Some(Individual)
  ))

  val authResultWithVatDec: Future[Enrolments ~ Option[AffinityGroup]] = Future.successful(new ~(
    Enrolments(Set(mtdEnrolment,vatDecEnrolment)),
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

  val exampleNonStandardTaxPeriods: Seq[TaxPeriod] = Seq(
      TaxPeriod("2018-12-29", "2018-12-30"),
      TaxPeriod("2018-12-31", "2019-01-01"),
      TaxPeriod("2019-01-02", "2019-01-03"),
      TaxPeriod("2019-01-04", "2019-01-05")
  )

  val exampleNonNSTP: Option[TaxPeriod] = Some(TaxPeriod("2019-01-06", "2019-01-31"))

  val navContent: NavContent = NavContent(
    NavLinks("Home", "Hafan", "http://localhost:9999/home"),
    NavLinks("Account", "Crfrif", "http://localhost:9999/account"),
    NavLinks("Messages", "Negeseuon", "http://localhost:9999/messages", Some(1)),
    NavLinks("Help", "Cymorth", "http://localhost:9999/help")
  )

  val penaltiesSummaryModel: PenaltiesSummary = PenaltiesSummary(
    noOfPoints = 3,
    noOfEstimatedPenalties = 0,
    noOfCrystalisedPenalties = 0,
    estimatedPenaltyAmount = 0,
    crystalisedPenaltyAmountDue = 0,
    hasAnyPenaltyData = true
  )

  val penaltySummaryResponse: HttpGetResult[PenaltiesSummary] = Right(penaltiesSummaryModel)
  val penaltySummaryNoResponse: HttpGetResult[PenaltiesSummary] = Left(PenaltiesFeatureSwitchError)

  val whatYouOweChargeModel: StandardChargeViewModel = StandardChargeViewModel(
    chargeType = "VAT Return Debit Charge",
    outstandingAmount = 10000,
    originalAmount = 1000.00,
    clearedAmount = Some(00.00),
    dueDate = LocalDate.parse("2019-03-03"),
    periodKey = Some("ABCD"),
    isOverdue = false,
    chargeReference = Some("XD002750002155"),
    periodFrom = Some(LocalDate.parse("2019-01-01")),
    periodTo = Some(LocalDate.parse("2019-02-02"))
  )

  val whatYouOweViewModel: WhatYouOweViewModel = WhatYouOweViewModel(
    10000,
    Seq(whatYouOweChargeModel),
    mandationStatus = "MTDfB"
  )

  val viewModelNoChargeDescription: WhatYouOweViewModel = whatYouOweViewModel.copy(
    charges = Seq(whatYouOweChargeModel.copy(
      chargeType = "VAT Miscellaneous Penalty"
    ))
  )

  val chargeModel1: StandardChargeViewModel = StandardChargeViewModel(
    "VAT Return Debit Charge",
    111.11,
    333.33,
    Some(222.22),
    LocalDate.parse("2018-03-01"),
    Some("18AA"),
    isOverdue = true,
    Some("ABCD"),
    Some(LocalDate.parse("2018-01-01")),
    Some(LocalDate.parse("2018-02-01"))
  )

  val chargeModel2: StandardChargeViewModel =
    chargeModel1.copy(
      chargeType = "VAT Carter Penalty",
      isOverdue = false,
      outstandingAmount = 456.00,
      dueDate = LocalDate.parse("2018-12-01")
    )

  val overdueCrystallisedInterestCharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2021-01-01"),
    periodTo = LocalDate.parse("2021-03-01"),
    chargeType = "Dummy Charge",
    interestRate = 17.4,
    dueDate = LocalDate.parse("2021-04-08"),
    interestAmount = 3333.33,
    amountReceived = 3333.33,
    leftToPay = 111.00,
    isOverdue = true,
    chargeReference = "ChargeRef",
    isPenalty = false
  )

  val crystallisedInterestCharge: CrystallisedInterestViewModel = overdueCrystallisedInterestCharge.copy(isOverdue = false)


  val whatYouOweViewModel2Charge: WhatYouOweViewModel =
    WhatYouOweViewModel(567.11, Seq(chargeModel1, chargeModel2, overdueCrystallisedInterestCharge), mandationStatus = "")

  val whatYouOweCharge: StandardChargeViewModel = StandardChargeViewModel(
    chargeType = "VAT Return Debit Charge",
    outstandingAmount = BigDecimal(1111.11),
    originalAmount = BigDecimal(3333.33),
    clearedAmount = Some(BigDecimal(2222.22)),
    dueDate = LocalDate.parse("2021-04-08"),
    periodKey = None,
    isOverdue = false,
    chargeReference = None,
    periodFrom = Some(LocalDate.parse("2021-01-01")),
    periodTo = Some(LocalDate.parse("2021-03-31"))
  )

  val whatYouOweChargeOverdue: StandardChargeViewModel = whatYouOweCharge.copy(isOverdue = true)

  val whatYouOweChargeNoPeriod: StandardChargeViewModel = whatYouOweCharge.copy(periodFrom = None, periodTo = None)

  val whatYouOweChargeNoPeriodFrom: StandardChargeViewModel = whatYouOweCharge.copy(periodFrom = None)

  val whatYouOweChargeNoPeriodTo: StandardChargeViewModel = whatYouOweCharge.copy(periodTo = None)

  val whatYouOweChargeNoClearedAmount: StandardChargeViewModel = whatYouOweCharge.copy(clearedAmount = None)

  val whatYouOweChargeNoViewReturn: StandardChargeViewModel = whatYouOweCharge.copy(chargeType = "VAT Repayment Supplement Rec")

  val whatYouOweUrl: String = testOnly.controllers.routes.WhatYouOweController.show.url

  val vatDetailsUrl: String = controllers.routes.VatDetailsController.details.url
}
