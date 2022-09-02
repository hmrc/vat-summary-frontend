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
import models._
import models.errors.PenaltiesFeatureSwitchError
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments._
import models.penalties.{LPPDetails, PenaltiesSummary, PenaltyDetails}
import models.viewModels._
import play.api.libs.json.{Format, JsObject, JsValue, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}
import java.time.{LocalDate, LocalDateTime}

import common.ChargeViewModelTypes._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

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
    ddCollectionInProgress = false,
    accruedInterestAmount = Some(BigDecimal(2))
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
    accruedInterestAmount = Some(BigDecimal(2)),
    originalAmount = Some(1000.00),
    clearedAmount = Some(00.00)
  )

  val paymentNoAccInterest: PaymentWithPeriod = payment.copy(accruedInterestAmount = Some(0))
  val unrepayableOverpayment: PaymentWithPeriod = payment.copy(chargeType = VatUnrepayableOverpayment)

  val paymentNoPeriodNoDate: PaymentNoPeriod = Payment(
    OADefaultInterestCharge,
    LocalDate.parse("2019-03-03"),
    BigDecimal("10000"),
    Some("ABCD"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruedInterestAmount = Some(BigDecimal(2)),
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
    ddCollectionInProgress = false,
    accruedInterestAmount = Some(BigDecimal(2))
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
    clearedAmount = 00.00,
    dueDate = LocalDate.parse("2019-03-03"),
    periodKey = Some("ABCD"),
    isOverdue = false,
    chargeReference = Some("XD002750002155"),
    periodFrom = Some(LocalDate.parse("2019-01-01")),
    periodTo = Some(LocalDate.parse("2019-02-02"))
  )

  val whatYouOweChargeModelEstimatedInterest: EstimatedInterestViewModel = EstimatedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = VatReturnLPI.value,
    interestRate = 5.00,
    interestAmount = BigDecimal(2),
    isPenalty = false
  )

  val wyoChargeUnrepayableOverpayment: StandardChargeViewModel = whatYouOweChargeModel.copy(chargeType = "VAT Unrepayable Overpayment")

  val whatYouOweChargeModelInterestCharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return LPI",
    interestRate = 5.00,
    dueDate = LocalDate.parse("2019-03-03"),
    interestAmount = 1000.00,
    amountReceived = 00.00,
    leftToPay = 10000,
    isOverdue = false,
    chargeReference = "XD002750002155",
    isPenalty = false
  )

  val penaltyInterestCharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return 1st LPP LPI",
    interestRate = 5.00,
    dueDate = LocalDate.parse("2019-03-03"),
    interestAmount = 1000.00,
    amountReceived = 00.00,
    leftToPay = 10000,
    isOverdue = false,
    chargeReference = "XD002750002155",
    isPenalty = true
  )

  val whatYouOweViewModel: WhatYouOweViewModel = WhatYouOweViewModel(
    10000,
    Seq(whatYouOweChargeModel),
    mandationStatus = "MTDfB",
    false
  )

  val whatYouOweViewModelWithEstimatedInterest: WhatYouOweViewModel = whatYouOweViewModel.copy(
    charges = Seq(whatYouOweChargeModel, whatYouOweChargeModelEstimatedInterest)
  )

  val whatYouOweViewModelInterestCharges: WhatYouOweViewModel = WhatYouOweViewModel(
    40000,
    Seq(whatYouOweChargeModel,
      whatYouOweChargeModelEstimatedInterest,
      whatYouOweChargeModel,
      whatYouOweChargeModelEstimatedInterest,
      whatYouOweChargeModelInterestCharge,
      penaltyInterestCharge
    ),
    mandationStatus = "MTDfB",
    false
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
    222.22,
    LocalDate.parse("2018-03-01"),
    Some("18AA"),
    isOverdue = true,
    Some("ABCD"),
    Some(LocalDate.parse("2018-01-01")),
    Some(LocalDate.parse("2018-02-01"))
  )

  val standardChargeModelMaxJson: JsObject = Json.obj(
    "chargeType" -> "VAT Return Debit Charge",
    "outstandingAmount" -> 111.11,
    "originalAmount" -> 333.33,
    "clearedAmount" -> 222.22,
    "dueDate" -> "2018-03-01",
    "periodKey" -> "18AA",
    "isOverdue" -> true,
    "chargeReference" -> "ABCD",
    "periodFrom" -> "2018-01-01",
    "periodTo" -> "2018-02-01"
  )

  val standardChargeModelMin: StandardChargeViewModel =
    chargeModel1.copy(periodKey = None, periodFrom = None, periodTo = None, chargeReference = None)

  val standardChargeModelMinJson: JsObject = Json.obj(
    "chargeType" -> "VAT Return Debit Charge",
    "outstandingAmount" -> 111.11,
    "originalAmount" -> 333.33,
    "clearedAmount" -> 222.22,
    "dueDate" -> "2018-03-01",
    "isOverdue" -> true
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
    chargeType = "VAT Central Assessment LPI",
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

  val crystallisedInterestJson: JsObject = Json.obj(
    "periodFrom" -> "2021-01-01",
    "periodTo" -> "2021-03-01",
    "chargeType" -> "VAT Central Assessment LPI",
    "interestRate" -> 17.4,
    "dueDate" -> "2021-04-08",
    "interestAmount" -> 3333.33,
    "amountReceived" -> 3333.33,
    "leftToPay" -> 111.00,
    "isOverdue" -> false,
    "chargeReference" -> "ChargeRef",
    "isPenalty" -> false
  )

  val estimatedInterestModel: EstimatedInterestViewModel = EstimatedInterestViewModel(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "VAT Central Assessment LPI",
    2.6,
    300.33,
    isPenalty = false
  )

  val estimatedInterestJson: JsObject = Json.obj(
    "periodFrom" -> "2018-01-01",
    "periodTo" -> "2018-02-02",
    "chargeType" -> "VAT Central Assessment LPI",
    "interestRate" -> 2.6,
    "interestAmount" -> 300.33,
    "isPenalty" -> false
  )

  val crystallisedLPP1Model: CrystallisedLPP1ViewModel = CrystallisedLPP1ViewModel(
    "99",
    "10",
    Some("20"),
    2.4,
    Some(2.6),
    111.11,
    Some(222.22),
    LocalDate.parse("2020-01-01"),
    500.55,
    100.11,
    400.44,
    LocalDate.parse("2020-03-03"),
    LocalDate.parse("2020-04-04"),
    "VAT Return 1st LPP",
    "CHARGEREF",
    isOverdue = false
  )

  val crystallisedLPP1JsonMax: JsObject = Json.obj(
    "numberOfDays" -> "99",
    "part1Days" -> "10",
    "part2Days" -> "20",
    "part1PenaltyRate" -> 2.4,
    "part2PenaltyRate" -> 2.6,
    "part1UnpaidVAT" -> 111.11,
    "part2UnpaidVAT" -> 222.22,
    "dueDate" -> "2020-01-01",
    "penaltyAmount" -> 500.55,
    "amountReceived" -> 100.11,
    "leftToPay" -> 400.44,
    "periodFrom" -> "2020-03-03",
    "periodTo" -> "2020-04-04",
    "chargeType" -> "VAT Return 1st LPP",
    "chargeReference" -> "CHARGEREF",
    "isOverdue" -> false
  )

  val crystallisedLPP1ModelMin: CrystallisedLPP1ViewModel =
    crystallisedLPP1Model.copy(part2Days = None, part2PenaltyRate = None, part2UnpaidVAT = None)

  val crystallisedLPP1JsonMin: JsObject = Json.obj(
    "numberOfDays" -> "99",
    "part1Days" -> "10",
    "part1PenaltyRate" -> 2.4,
    "part1UnpaidVAT" -> 111.11,
    "dueDate" -> "2020-01-01",
    "penaltyAmount" -> 500.55,
    "amountReceived" -> 100.11,
    "leftToPay" -> 400.44,
    "periodFrom" -> "2020-03-03",
    "periodTo" -> "2020-04-04",
    "chargeType" -> "VAT Return 1st LPP",
    "chargeReference" -> "CHARGEREF",
    "isOverdue" -> false
  )

  val crystallisedLPP2Model: CrystallisedLPP2ViewModel = CrystallisedLPP2ViewModel(
    "31",
    4.0,
    LocalDate.parse("2020-01-01"),
    130.13,
    0,
    130.13,
    LocalDate.parse("2020-03-03"),
    LocalDate.parse("2020-04-04"),
    "VAT OA 2nd LPP LPI",
    "CHARGEREF",
    isOverdue = false
  )

  val crystallisedLPP2Json: JsObject = Json.obj(
    "day" -> "31",
    "interestRate" -> 4.0,
    "dueDate" -> "2020-01-01",
    "penaltyAmount" -> 130.13,
    "amountReceived" -> 0,
    "leftToPay" -> 130.13,
    "periodFrom" -> "2020-03-03",
    "periodTo" -> "2020-04-04",
    "chargeType" -> "VAT OA 2nd LPP LPI",
    "chargeReference" -> "CHARGEREF",
    "isOverdue" -> false
  )

  val estimatedLPP1Model: EstimatedLPP1ViewModel = EstimatedLPP1ViewModel(
    "10", "20", 2.2, 4.4, 500.55, 30.33, LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-02"), "VAT Return 1st LPP"
  )

  val estimatedLPP1Json: JsObject = Json.obj(
    "part1Days" -> "10",
    "part2Days" -> "20",
    "part1PenaltyRate" -> 2.2,
    "part2PenaltyRate" -> 4.4,
    "part1UnpaidVAT" -> 500.55,
    "penaltyAmount" -> 30.33,
    "periodFrom" -> "2020-01-01",
    "periodTo" -> "2020-02-02",
    "chargeType" -> "VAT Return 1st LPP"
  )

  val estimatedLPP2Model: EstimatedLPP2ViewModel = EstimatedLPP2ViewModel(
    "31", 4.4, 4.22, LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-02"), "VAT Return 1st LPP"
  )

  val estimatedLPP2Json: JsObject = Json.obj(
    "day" -> "31",
    "penaltyRate" -> 4.4,
    "penaltyAmount" -> 4.22,
    "periodFrom" -> "2020-01-01",
    "periodTo" -> "2020-02-02",
    "chargeType" -> "VAT Return 1st LPP"
  )

  val whatYouOweViewModel2Charge: WhatYouOweViewModel =
    WhatYouOweViewModel(567.11, Seq(chargeModel1, chargeModel2, overdueCrystallisedInterestCharge), mandationStatus = "", true)

  val whatYouOweCharge: StandardChargeViewModel = StandardChargeViewModel(
    chargeType = "VAT Return Debit Charge",
    outstandingAmount = BigDecimal(1111.11),
    originalAmount = BigDecimal(3333.33),
    clearedAmount = BigDecimal(2222.22),
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

  val whatYouOweChargeNoClearedAmount: StandardChargeViewModel = whatYouOweCharge.copy(clearedAmount = 0)

  val whatYouOweChargeNoViewReturn: StandardChargeViewModel = whatYouOweCharge.copy(chargeType = "VAT Repayment Supplement Rec")

  val whatYouOweUrl: String = testOnly.controllers.routes.WhatYouOweController.show.url

  val vatDetailsUrl: String = controllers.routes.VatDetailsController.details.url

  val LPPDetailsModelMax: LPPDetails = LPPDetails(
    principalChargeReference = "ABCDEFGHIJKLMNOP",
    penaltyCategory = "LPP1",
    Some(100.11),
    Some("15"),
    Some(2.4),
    Some(200.22),
    Some("30"),
    Some(4.2),
    Some("31"),
    Some(5.5),
    penaltyChargeReference = Some("BCDEFGHIJKLMNOPQ")
  )

  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(LPPDetailsModelMax)
  )

  val LPPDetailsModelMin: LPPDetails = LPPDetails(
    principalChargeReference = "ABCDEFGHIJKLMNOP",
    penaltyCategory = "LPP1",
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val penaltyDetailsModelMin: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(LPPDetailsModelMin)
  )

  val LPPDetailsJsonMax: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5,
    "penaltyChargeReference" -> "BCDEFGHIJKLMNOPQ"
  )

  val penaltyDetailsResponse: HttpGetResult[PenaltyDetails] = Right(penaltyDetailsModelMax)

  val LPPDetailsJsonMin: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1"
  )

  val penaltyDetailsJsonMax : JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPDetailsJsonMax)
  )

  val penaltyDetailsJsonMin : JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPDetailsJsonMin)
  )

  implicit val dateFormat: Format[LocalDateTime] = MongoJavatimeFormats.localDateTimeFormat
  val time: LocalDateTime = LocalDateTime.parse("2022-07-15T14:42:50.125")

  def wyoDBModel(modelType: String, json: JsValue): WYODatabaseModel = WYODatabaseModel(
    "testId",
    modelType,
    json,
    time
  )

  val wyoStandardDBModel: WYODatabaseModel = wyoDBModel(standard, standardChargeModelMaxJson)
  val wyoEstimatedIntDBModel: WYODatabaseModel = wyoDBModel(estimated, estimatedInterestJson)
  val wyoCrystallisedIntDBModel: WYODatabaseModel = wyoDBModel(crystallised, crystallisedInterestJson)
  val wyoCrystallisedLPP1DBModel: WYODatabaseModel = wyoDBModel(crystallisedLPP1, crystallisedLPP1JsonMax)

  val wyoDBjsonModel: JsObject = Json.obj(
    "_id" -> "testId",
    "modelType" -> "StandardChargeViewModel",
    "data" -> standardChargeModelMaxJson,
    "creationTimestamp" -> time
  )

}
