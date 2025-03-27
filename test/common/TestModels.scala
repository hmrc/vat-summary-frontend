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

package common

import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models._
import models.errors.PenaltiesFeatureSwitchError
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{VATOverpaymentforTaxLPI, _}
import models.penalties.{LPPDetails, PenaltiesSummary, PenaltyDetails}
import models.viewModels._
import play.api.libs.json.{Format, JsObject, JsValue, Json}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}

import java.time.{Instant, LocalDate}

import common.ChargeViewModelTypes._
import models.ESSTTP.TTPResponseModel
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import scala.concurrent.Future

object TestModels {

  val testDate: LocalDate = LocalDate.parse("2018-05-01")

  val payments: Payments = Payments(Seq(Payment(
    ReturnDebitCharge,
    Some(LocalDate.parse("2019-01-01")),
    Some(LocalDate.parse("2019-02-02")),
    LocalDate.parse("2019-03-03"),
    1,
    Some("#001"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = Some(BigDecimal(2)),
    accruingPenaltyAmount = None,
    penaltyType = None,
    originalAmount = BigDecimal(10000),
    clearedAmount = None
  )))

  val payment: PaymentWithPeriod = PaymentWithPeriod(
    chargeType = ReturnDebitCharge,
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    due = LocalDate.parse("2019-03-03"),
    outstandingAmount = 10000,
    periodKey = Some("ABCD"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = Some(BigDecimal(2)),
    accruingPenaltyAmount = Some(50.55),
    penaltyType = Some("LPP1"),
    originalAmount = BigDecimal(10000),
    clearedAmount = Some(0)
  )

  val overpaymentForRPI: PaymentWithPeriod = PaymentWithPeriod(
    VatOverpaymentForRPI,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    LocalDate.parse("2019-03-03"),
    10000,
    None,
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = None,
    originalAmount = BigDecimal(10000),
    clearedAmount = Some(0),
    accruingPenaltyAmount = None,
    penaltyType = None
  )

  val paymentNoAccInterest: PaymentWithPeriod =
    payment.copy(accruingInterestAmount = Some(0), accruingPenaltyAmount = None)
  val unrepayableOverpayment: PaymentWithPeriod =
    payment.copy(chargeType = VatUnrepayableOverpayment, accruingPenaltyAmount = None)

  val overpaymentforTax: PaymentWithPeriod =
    payment.copy(chargeType = VATOverpaymentforTax, accruingPenaltyAmount = None, penaltyType = None)

  val vatInaccAssessPen: PaymentWithPeriod =
    payment.copy(chargeType = InaccuraciesAssessmentsPenCharge, accruingPenaltyAmount = None, penaltyType = None)

  val paymentNoPeriodNoDate: PaymentNoPeriod = PaymentNoPeriod(
    OADefaultInterestCharge,
    LocalDate.parse("2019-03-03"),
    BigDecimal("10000"),
    Some("ABCD"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = Some(BigDecimal(2)),
    accruingPenaltyAmount = None,
    penaltyType = None,
    originalAmount = BigDecimal(10000),
    clearedAmount = Some(00.00)
  )

  val defaultInterestPaymentNoPeriod: PaymentNoPeriod = paymentNoPeriodNoDate.copy(chargeType = VatDefaultInterestDebit)

  val paymentWithDifferentAgentMessage: PaymentWithPeriod = payment.copy(chargeType = BnpRegPost2010Charge)

  val paymentOnAccount: PaymentNoPeriod = PaymentNoPeriod(
    PaymentOnAccount,
    LocalDate.parse("2017-01-01"),
    BigDecimal("0"),
    None,
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = Some(BigDecimal(2)),
    accruingPenaltyAmount = None,
    penaltyType = None,
    originalAmount = BigDecimal(10000),
    clearedAmount = None
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

  val duplicateObligations: VatReturnObligations = VatReturnObligations(Seq(VatReturnObligation(
    LocalDate.parse("2019-04-04"),
    LocalDate.parse("2019-05-05"),
    LocalDate.parse("2019-06-06"),
    "O",
    None,
    "#001"
  ),
    VatReturnObligation(
      LocalDate.parse("2019-04-04"),
      LocalDate.parse("2019-05-05"),
      LocalDate.parse("2019-06-06"),
      "O",
      None,
      "#001")
  ))

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
    Some(ChangeIndicators(deregister = false)),
    isMissingTrader = false,
    hasPendingPpobChanges = false,
    mandationStatus = "MTDfB"
  )

  val customerMigrated2018: CustomerInformation =
    customerInformationMax.copy(customerMigratedToETMPDate = Some("2018-05-01"), hybridToFullMigrationDate = None)

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
    Some("2019-03-03"), Some("2019-03-03"), Some(entityName), currentDate = testDate, partyType = Some("1"), mandationStatus = "2",
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

  val penaltySummaryResponse: HttpResult[PenaltiesSummary] = Right(penaltiesSummaryModel)
  val penaltySummaryNoResponse: HttpResult[PenaltiesSummary] = Left(PenaltiesFeatureSwitchError)

  val whatYouOweChargeModel: StandardChargeViewModel = StandardChargeViewModel(
    chargeType = "VAT Return Debit Charge",
    outstandingAmount = 10000.00,
    originalAmount = 10000.00,
    clearedAmount = 00.00,
    dueDate = LocalDate.parse("2019-03-03"),
    periodKey = Some("ABCD"),
    isOverdue = false,
    chargeReference = Some("XD002750002155"),
    periodFrom = Some(LocalDate.parse("2019-01-01")),
    periodTo = Some(LocalDate.parse("2019-02-02")),
    directDebitMandateFound = false
  )

  val whatYouOweChargeModelEstimatedLPI: EstimatedInterestViewModel = EstimatedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = VatReturnLPI.value,
    interestAmount = BigDecimal(2),
    isPenaltyReformPenaltyLPI = false,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
  )

  val vatOverpaymentTax: StandardChargeViewModel = whatYouOweChargeModel.copy(
    chargeType = "VAT Overpayment for Tax",
    isOverdue = false,
    outstandingAmount = 10000,
    originalAmount = 10000,
    clearedAmount = 0
  )

  val vatInaccAssessPenViewModel: StandardChargeViewModel = whatYouOweChargeModel.copy(
    chargeType = InaccuraciesAssessmentsPenCharge.value,
    isOverdue = false,
    outstandingAmount = 10000,
    originalAmount = 10000,
    clearedAmount = 0
  )

  val vatOverpaymentTaxLPIEstimatedModel: EstimatedInterestViewModel = whatYouOweChargeModelEstimatedLPI.copy(chargeType = "VAT Overpayment for Tax LPI")

  val wyoChargeUnrepayableOverpayment: StandardChargeViewModel = whatYouOweChargeModel.copy(chargeType = "VAT Unrepayable Overpayment")

  val whatYouOweChargeModelLPICharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return LPI",
    dueDate = LocalDate.parse("2019-03-03"),
    interestAmount = 10000.00,
    amountReceived = 0.00,
    leftToPay = 10000.00,
    isOverdue = false,
    chargeReference = "XD002750002155",
    isPenaltyReformPenaltyLPI = false,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
  )

  val penaltyReformPenaltyLPICharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return 1st LPP LPI",
    dueDate = LocalDate.parse("2019-03-03"),
    interestAmount = 1000.00,
    amountReceived = 00.00,
    leftToPay = 10000.00,
    isOverdue = false,
    chargeReference = "XD002750002155",
    isPenaltyReformPenaltyLPI = true,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
  )

  val lateSubmissionPenaltyCharge: LateSubmissionPenaltyViewModel = LateSubmissionPenaltyViewModel(
    chargeType = "VAT Late Submission Pen",
    dueDate =  LocalDate.parse("2019-03-03"),
    penaltyAmount = 10000.00,
    amountReceived = 0.00,
    leftToPay = 10000.00,
    isOverdue = false,
    chargeReference = "XD002750002155",
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    directDebitMandateFound = false
  )

  val whatYouOweViewModel: WhatYouOweViewModel = WhatYouOweViewModel(
    10000.00,
    Seq(whatYouOweChargeModel),
    mandationStatus = "MTDfB",
    containsOverduePayments = false,
    breathingSpace = false,
    directDebitMandateFound = false
  )

  val whatYouOweViewModelWithEstimatedLPI: WhatYouOweViewModel = whatYouOweViewModel.copy(
    totalAmount = 10002.00,
    charges = Seq(whatYouOweChargeModel, whatYouOweChargeModelEstimatedLPI)
  )

  val viewModelNoChargeDescription: WhatYouOweViewModel = whatYouOweViewModel.copy(
    charges = Seq(whatYouOweChargeModel.copy(
      chargeType = "VAT FTN Each Partner"
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
    Some(LocalDate.parse("2018-02-01")),
    directDebitMandateFound = false
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
    "periodTo" -> "2018-02-01",
    "directDebitMandateFound" -> false
  )

  val standardChargeModelMin: StandardChargeViewModel =
    chargeModel1.copy(periodKey = None, periodFrom = None, periodTo = None, chargeReference = None)

  val standardChargeModelMinJson: JsObject = Json.obj(
    "chargeType" -> "VAT Return Debit Charge",
    "outstandingAmount" -> 111.11,
    "originalAmount" -> 333.33,
    "clearedAmount" -> 222.22,
    "dueDate" -> "2018-03-01",
    "isOverdue" -> true,
    "directDebitMandateFound" -> false
  )

  val chargeModel2: StandardChargeViewModel =
    chargeModel1.copy(
      chargeType = "VAT Carter Penalty",
      isOverdue = false,
      outstandingAmount = 456.00,
      dueDate = LocalDate.parse("2018-12-01")
    )

  val vatOverpaymentForRPIJson: JsObject = Json.obj(
    "periodFrom" -> "2018-04-07",
    "periodTo" -> "2018-04-10",
    "chargeType" -> "VAT Overpayment for RPI",
    "dueDate" -> "2018-04-15",
    "correctionCharge" -> 200.00,
    "amountReceived" -> 40.00,
    "leftToPay" -> 160.00,
    "isOverdue" -> false,
    "chargeReference" -> "ABCD",
    "directDebitMandateFound" -> false
  )

  val vatOverpaymentForRPI: VatOverpaymentForRPIViewModel = VatOverpaymentForRPIViewModel(
    periodFrom = LocalDate.parse("2018-04-07"),
    periodTo = LocalDate.parse("2018-04-10"),
    chargeType = "VAT Overpayment for RPI",
    dueDate = LocalDate.parse("2018-04-15"),
    correctionCharge = 200.00,
    amountReceived = 40.00,
    leftToPay = 160.00,
    isOverdue = false,
    chargeReference = Some("ABCD"),
    directDebitMandateFound = false
  )

  val overdueCrystallisedLPICharge: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    periodFrom = LocalDate.parse("2021-01-01"),
    periodTo = LocalDate.parse("2021-03-01"),
    chargeType = "VAT Central Assessment LPI",
    dueDate = LocalDate.parse("2021-04-08"),
    interestAmount = 3333.33,
    amountReceived = 3333.33,
    leftToPay = 111.00,
    isOverdue = true,
    chargeReference = "ChargeRef",
    isPenaltyReformPenaltyLPI = false,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
  )

  val crystallisedLPICharge: CrystallisedInterestViewModel = overdueCrystallisedLPICharge.copy(isOverdue = false)

  val crystallisedLPIJson: JsObject = Json.obj(
    "periodFrom" -> "2021-01-01",
    "periodTo" -> "2021-03-01",
    "chargeType" -> "VAT Central Assessment LPI",
    "dueDate" -> "2021-04-08",
    "interestAmount" -> 3333.33,
    "amountReceived" -> 3333.33,
    "leftToPay" -> 111.00,
    "isOverdue" -> false,
    "chargeReference" -> "ChargeRef",
    "isPenaltyReformPenaltyLPI" -> false,
    "isNonPenaltyReformPenaltyLPI" -> false,
    "directDebitMandateFound" -> false
  )

  val estimatedLPIModel: EstimatedInterestViewModel = EstimatedInterestViewModel(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "VAT Central Assessment LPI",
    300.33,
    isPenaltyReformPenaltyLPI = false,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
  )

  val estimatedLPIJson: JsObject = Json.obj(
    "periodFrom" -> "2018-01-01",
    "periodTo" -> "2018-02-02",
    "chargeType" -> "VAT Central Assessment LPI",
    "interestAmount" -> 300.33,
    "isPenaltyReformPenaltyLPI" -> false,
    "isNonPenaltyReformPenaltyLPI" -> false,
    "directDebitMandateFound" -> false
  )

  val crystallisedPenaltyModel: CrystallisedLPP1ViewModel = CrystallisedLPP1ViewModel(
    numberOfDays = "30",
    part1Days = "15",
    part2Days = Some("30"),
    part1PenaltyRate = 2.4,
    part2PenaltyRate = Some(4.2),
    part1UnpaidVAT = 100.11,
    part2UnpaidVAT = Some(200.22),
    dueDate = LocalDate.parse("2019-03-03"),
    penaltyAmount = 10000.00,
    amountReceived = 0.00,
    leftToPay = 10000.00,
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return 1st LPP",
    chargeReference = "XD002750002155",
    isOverdue = false,
    directDebitMandateFound = false
  )

  val estimatedLPIPenalty: EstimatedInterestViewModel = EstimatedInterestViewModel(
    periodFrom = LocalDate.parse("2019-01-01"),
    periodTo = LocalDate.parse("2019-02-02"),
    chargeType = "VAT Return 1st LPP LPI",
    interestAmount = 2,
    isPenaltyReformPenaltyLPI = true,
    isNonPenaltyReformPenaltyLPI = false,
    directDebitMandateFound = false
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
    isOverdue = false,
    directDebitMandateFound = false
  )

  val crystallisedVatOPLPP1Model: CrystallisedLPP1ViewModel =
    crystallisedPenaltyModel.copy(
      chargeType = VatOverpayments1stLPP.value,
      chargeReference = "BCDEFGHIJKLMNOPQ"
    )

  val estimatedVatOPLPP1LPI: EstimatedInterestViewModel =
    EstimatedInterestViewModel(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VatOverpayments1stLPPLPI.value,
      interestAmount = BigDecimal(2),
      isPenaltyReformPenaltyLPI = true,
      isNonPenaltyReformPenaltyLPI = false,
      directDebitMandateFound = false
    )

  val crystallisedVatOPLPP1LPIModel: CrystallisedInterestViewModel =
    penaltyReformPenaltyLPICharge.copy(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VatOverpayments1stLPPLPI.value,
      interestAmount = BigDecimal(10000),
      chargeReference = "BCDEFGHIJKLMNOPQ",
      isPenaltyReformPenaltyLPI = true
    )

  val estimatedVATOverpaymentforTaxLPI: EstimatedInterestViewModel =
    EstimatedInterestViewModel(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VATOverpaymentforTaxLPI.value,
      interestAmount = BigDecimal(2),
      isPenaltyReformPenaltyLPI = false,
      isNonPenaltyReformPenaltyLPI = false,
      directDebitMandateFound = false
    )

  val crystallisedVATOverpaymentforTaxLPI: CrystallisedInterestViewModel =
    penaltyReformPenaltyLPICharge.copy(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VATOverpaymentforTaxLPI.value,
      interestAmount = 10000,
      amountReceived = 0,
      leftToPay = 10000,
      chargeReference = "BCDEFGHIJKLMNOPQ",
      isPenaltyReformPenaltyLPI = false,
      isNonPenaltyReformPenaltyLPI = false
    )

  val estimatedVATInaccAssessPenLPIModel: EstimatedInterestViewModel =
    estimatedVATOverpaymentforTaxLPI.copy(
      chargeType = VatInaccuracyAssessPenLPI.value,
      interestAmount = BigDecimal(2),
      isNonPenaltyReformPenaltyLPI = true
    )

  val crystallisedVATInaccAssessPenLPIModel: CrystallisedInterestViewModel =
    crystallisedVATOverpaymentforTaxLPI.copy(
      chargeType = VatInaccuracyAssessPenLPI.value,
      isNonPenaltyReformPenaltyLPI = true
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
    "isOverdue" -> false,
    "directDebitMandateFound" -> false
  )

  val crystallisedLPP1ModelMin: CrystallisedLPP1ViewModel =
    crystallisedLPP1Model.copy(part2Days = None, part2PenaltyRate = None, part2UnpaidVAT = None)

  val overpaymentforTaxLPP1: PaymentWithPeriod =
    payment.copy(chargeType = VatOverpayments1stLPP,
      chargeReference = Some("BCDEFGHIJKLMNOPQ"),
      accruingInterestAmount = None,
      accruingPenaltyAmount = None
    )

  val overpaymentForTaxLPP1LPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VatOverpayments1stLPPLPI,
      chargeReference = Some("BCDEFGHIJKLMNOPQ")
    )

  val overpaymentForTaxLPP1EstLPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VatOverpayments1stLPP,
      chargeReference = Some("BCDEFGHIJKLMNOPQ"),
      accruingPenaltyAmount = None
    )

  val overpaymentforTaxLPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VATOverpaymentforTaxLPI,
      chargeReference = Some("BCDEFGHIJKLMNOPQ"),
      accruingPenaltyAmount = None
    )

  val vatInaccAssessPenLPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VatInaccuracyAssessPenLPI,
      outstandingAmount = 10000,
      chargeReference = Some("BCDEFGHIJKLMNOPQ"),
      accruingInterestAmount = None,
      accruingPenaltyAmount = None,
      penaltyType = None,
      originalAmount = BigDecimal(10000),
      clearedAmount = Some(0)
    )

  val overpaymentForTaxLPP2LPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VatOverpayments2ndLPPLPI,
      chargeReference = Some("BCDEFGHIJKLMNOPQ"),
      penaltyType = Some("LPP2")
    )

  val overpaymentForTaxLPP2EstLPI: PaymentWithPeriod =
    payment.copy(
      chargeType = VatOverpayments2ndLPP,
      chargeReference = Some("ABCDEFGHIJKL"),
      accruingPenaltyAmount = None,
      penaltyType = Some("LPP2")
    )

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
    "isOverdue" -> false,
    "directDebitMandateFound" -> false
  )

  val crystallisedLPP2Model: CrystallisedLPP2ViewModel = CrystallisedLPP2ViewModel(
    "31",
    4.0,
    LocalDate.parse("2020-01-01"),
    130.13,
    0.00,
    130.13,
    LocalDate.parse("2020-03-03"),
    LocalDate.parse("2020-04-04"),
    "VAT AA 2nd LPP",
    "CHARGEREF",
    isOverdue = false,
    directDebitMandateFound = false
  )

  val crystallisedLPP2ModelMax: CrystallisedLPP2ViewModel = CrystallisedLPP2ViewModel(
    "31",
    5.5,
    LocalDate.parse("2019-03-03"),
    10000,
    0,
    10000,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    "VAT AA 2nd LPP",
    "CHARGEREF",
    isOverdue = false,
    directDebitMandateFound = false
  )

  val overpaymentforTaxLPP2: PaymentWithPeriod =
    payment.copy(
      chargeType = VatOverpayments2ndLPP,
      accruingInterestAmount = None,
      accruingPenaltyAmount = None,
      chargeReference = Some("ABCDEFGHIJKL"),
      penaltyType = Some("LPP2")
    )

  val crystallisedVatOPLPP2Model: CrystallisedLPP2ViewModel =
    crystallisedLPP2ModelMax.copy(
      chargeType = VatOverpayments2ndLPP.value,
      chargeReference = "ABCDEFGHIJKL"
    )

  val estimatedVatOPLPP2LPI: EstimatedInterestViewModel =
    EstimatedInterestViewModel(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VatOverpayments2ndLPPLPI.value,
      interestAmount = BigDecimal(2),
      isPenaltyReformPenaltyLPI = true,
      isNonPenaltyReformPenaltyLPI = false,
      directDebitMandateFound = false
    )

  val crystallisedVatOPLPP2LPIModel: CrystallisedInterestViewModel =
    penaltyReformPenaltyLPICharge.copy(
      periodFrom = crystallisedPenaltyModel.periodFrom,
      periodTo = crystallisedPenaltyModel.periodTo,
      chargeType = VatOverpayments2ndLPPLPI.value,
      interestAmount = BigDecimal(10000),
      chargeReference = "BCDEFGHIJKLMNOPQ",
      isPenaltyReformPenaltyLPI = true
    )

  val crystallisedLPP2Json: JsObject = Json.obj(
    "numberOfDays" -> "31",
    "penaltyRate" -> 4.0,
    "dueDate" -> "2020-01-01",
    "penaltyAmount" -> 130.13,
    "amountReceived" -> 0.00,
    "leftToPay" -> 130.13,
    "periodFrom" -> "2020-03-03",
    "periodTo" -> "2020-04-04",
    "chargeType" -> "VAT AA 2nd LPP",
    "chargeReference" -> "CHARGEREF",
    "isOverdue" -> false,
    "directDebitMandateFound" -> false
  )

  val estimatedLPP1Model: EstimatedLPP1ViewModel = EstimatedLPP1ViewModel(
    "15",
    "30",
    2.4,
    4.2,
    100.11,
    50.55,
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    "VAT Return 1st LPP",
    timeToPayPlan = false,
    breathingSpace = false,
    directDebitMandateFound = false
  )

  val estimatedLPP1Json: JsObject = Json.obj(
    "part1Days" -> "15",
    "part2Days" -> "30",
    "part1PenaltyRate" -> 2.4,
    "part2PenaltyRate" -> 4.2,
    "part1UnpaidVAT" -> 100.11,
    "penaltyAmount" -> 50.55,
    "periodFrom" -> "2019-01-01",
    "periodTo" -> "2019-02-02",
    "chargeType" -> "VAT Return 1st LPP",
    "timeToPayPlan" -> false,
    "breathingSpace" -> false,
    "directDebitMandateFound" -> false
  )

  val estimatedLPP2Model: EstimatedLPP2ViewModel = EstimatedLPP2ViewModel(
    "31", 4.4, 4.22, LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-02"), "VAT AA 2nd LPP", timeToPay = false, breathingSpace = false, directDebitMandateFound = false
  )
  val estimatedLPP2ModelTTP: EstimatedLPP2ViewModel = estimatedLPP2Model.copy(timeToPay = true)

  val estimatedLPP2Json: JsObject = Json.obj(
    "day" -> "31",
    "penaltyRate" -> 4.4,
    "penaltyAmount" -> 4.22,
    "periodFrom" -> "2020-01-01",
    "periodTo" -> "2020-02-02",
    "chargeType" -> "VAT AA 2nd LPP",
    "timeToPay" -> false,
    "breathingSpace" -> false,
    "directDebitMandateFound" -> false
  )

  val lateSubmissionPenaltyModel: LateSubmissionPenaltyViewModel = LateSubmissionPenaltyViewModel(
    "VAT Late Submission Pen",
    LocalDate.parse("2020-10-01"),
    100.55,
    0.00,
    100.55,
    isOverdue = false,
    "CHARGEREF",
    LocalDate.parse("2020-05-05"),
    LocalDate.parse("2020-06-06"),
    directDebitMandateFound = false
  )

  val lateSubmissionPenaltyJson: JsObject = Json.obj(
    "chargeType" -> "VAT Late Submission Pen",
    "dueDate" -> "2020-10-01",
    "penaltyAmount" -> 100.55,
    "amountReceived" -> 0.00,
    "leftToPay" -> 100.55,
    "isOverdue" -> false,
    "chargeReference" -> "CHARGEREF",
    "periodFrom" -> "2020-05-05",
    "periodTo" -> "2020-06-06",
    "directDebitMandateFound" -> false
  )

  val whatYouOweViewModel2Charge: WhatYouOweViewModel = WhatYouOweViewModel(
    567.11,
    Seq(chargeModel1, chargeModel2, overdueCrystallisedLPICharge),
    mandationStatus = "",
    containsOverduePayments = true,
    breathingSpace = false,
    directDebitMandateFound = false
  )

  val whatYouOweViewModelBreathingSpace: WhatYouOweViewModel = whatYouOweViewModel.copy(
    breathingSpace = true
  )
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
    periodTo = Some(LocalDate.parse("2021-03-31")),
    directDebitMandateFound = false
  )

  val whatYouOweViewModelMultipleTypes: WhatYouOweViewModel = WhatYouOweViewModel(
    30052.55,
    Seq(
      whatYouOweChargeModel,
      whatYouOweChargeModelEstimatedLPI,
      estimatedLPP1Model,
      whatYouOweChargeModelLPICharge,
      lateSubmissionPenaltyCharge
    ),
    mandationStatus = "MTDfB",
    containsOverduePayments = false,
    breathingSpace = false,
    directDebitMandateFound = false
  )

  val whatYouOweChargeOverdue: StandardChargeViewModel = whatYouOweCharge.copy(isOverdue = true)

  val whatYouOweChargeNoPeriod: StandardChargeViewModel = whatYouOweCharge.copy(periodFrom = None, periodTo = None)

  val whatYouOweChargeNoPeriodFrom: StandardChargeViewModel = whatYouOweCharge.copy(periodFrom = None)

  val whatYouOweChargeNoPeriodTo: StandardChargeViewModel = whatYouOweCharge.copy(periodTo = None)

  val whatYouOweChargeNoClearedAmount: StandardChargeViewModel = whatYouOweCharge.copy(clearedAmount = 0.00)

  val whatYouOweChargeNoViewReturn: StandardChargeViewModel = whatYouOweCharge.copy(chargeType = "VAT Repayment Supplement Rec")

  val whatYouOweUrl: String = controllers.routes.WhatYouOweController.show.url

  val vatDetailsUrl: String = controllers.routes.VatDetailsController.details.url

  val LPPDetailsModelMaxWithLPP1HRPercentage: LPPDetails = LPPDetails(
    principalChargeReference = "XD002750002155",
    penaltyCategory = "LPP1",
    LPP1LRCalculationAmount = Some(100.11),
    LPP1LRDays = Some("15"),
    LPP1LRPercentage = Some(2.4),
    LPP1HRCalculationAmount = Some(200.22),
    LPP1HRDays = Some("30"),
    LPP1HRPercentage = Some(4.2),
    LPP2Days = Some("31"),
    LPP2Percentage = Some(5.5),
    penaltyChargeReference = Some("BCDEFGHIJKLMNOPQ"),
    timeToPay = false
  )

  val LPPDetailsModelMaxWithoutLPP1HRPercentage: LPPDetails = LPPDetails(
    principalChargeReference = "XD002750002155",
    penaltyCategory = "LPP1",
    Some(100.11),
    Some("15"),
    Some(2.4),
    Some(200.22),
    Some("30"),
    None,
    Some("31"),
    Some(5.5),
    penaltyChargeReference = Some("BCDEFGHIJKLMNOPQ"),
    timeToPay = false
  )

  val LPPLPP2DetailsModelMax: LPPDetails = LPPDetails(
    principalChargeReference = "XD002750002156",
    penaltyCategory = "LPP2",
    LPP1LRCalculationAmount = Some(100.11),
    LPP1LRDays = Some("15"),
    LPP1LRPercentage = Some(2.4),
    LPP1HRCalculationAmount = Some(200.22),
    LPP1HRDays = Some("30"),
    LPP1HRPercentage = Some(4.2),
    LPP2Days = Some("31"),
    LPP2Percentage = Some(5.5),
    penaltyChargeReference = Some("ABCDEFGHIJKL"),
    timeToPay = false
  )

  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(LPPDetailsModelMaxWithLPP1HRPercentage),
    breathingSpace = false
  )

  val penaltyDetailsLPP2ModelMax: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(LPPLPP2DetailsModelMax),
    breathingSpace = false
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
    None,
    timeToPay = false
  )

  val penaltyDetailsModelMin: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(),
    breathingSpace = false
  )

  val LPPDetailsJsonMax: JsObject = Json.obj(
    "principalChargeReference" -> "XD002750002155",
    "penaltyCategory" -> "LPP1",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5,
    "penaltyChargeReference" -> "BCDEFGHIJKLMNOPQ",
    "timeToPay" -> false
  )

  val penaltyDetailsResponse: HttpResult[PenaltyDetails] = Right(penaltyDetailsModelMax)

  val LPPDetailsJsonMin: JsObject = Json.obj(
    "principalChargeReference" -> "ABCDEFGHIJKLMNOP",
    "penaltyCategory" -> "LPP1",
    "timeToPay" -> false
  )

  val penaltyDetailsJsonMax : JsObject = Json.obj(
    "LPPDetails" -> Json.arr(LPPDetailsJsonMax),
    "breathingSpace" -> false
  )

  val penaltyDetailsJsonMin : JsObject = Json.obj(
    "LPPDetails" -> Json.arr(),
    "breathingSpace" -> false
  )

  implicit val dateFormat: Format[Instant] = MongoJavatimeFormats.instantFormat
  val time: Instant = Instant.parse("2022-07-15T14:42:50.00Z")

  def wyoDBModel(modelType: String, json: JsValue): WYODatabaseModel = WYODatabaseModel(
    "testId",
    modelType,
    json,
    time
  )

  val wyoStandardDBModel: WYODatabaseModel = wyoDBModel(standard, standardChargeModelMaxJson)
  val wyoOverpaymentForRPIViewModel: WYODatabaseModel = wyoDBModel(repaymentInterestCorrection, vatOverpaymentForRPIJson)
  val wyoEstimatedIntDBModel: WYODatabaseModel = wyoDBModel(estimatedInterest, estimatedLPIJson)
  val wyoEstimatedLPP1DBModel: WYODatabaseModel = wyoDBModel(estimatedLPP1, estimatedLPP1Json)
  val wyoEstimatedLPP2DBModel: WYODatabaseModel = wyoDBModel(estimatedLPP2, estimatedLPP2Json)
  val wyoCrystallisedIntDBModel: WYODatabaseModel = wyoDBModel(crystallisedInterest, crystallisedLPIJson)
  val wyoCrystallisedLPP1DBModel: WYODatabaseModel = wyoDBModel(crystallisedLPP1, crystallisedLPP1JsonMax)
  val wyoCrystallisedLPP2DBModel: WYODatabaseModel = wyoDBModel(crystallisedLPP2, crystallisedLPP2Json)
  val wyoLSPDBModel: WYODatabaseModel = wyoDBModel(lsp, lateSubmissionPenaltyJson)

  val wyoDBjsonModel: JsObject = Json.obj(
    "_id" -> "testId",
    "modelType" -> "StandardChargeViewModel",
    "data" -> standardChargeModelMaxJson,
    "creationTimestamp" -> time
  )
  val timeToPayResponseModel : TTPResponseModel = TTPResponseModel("592d4a09cdc8e04b00021459","http://localhost:1111/test")

  val timeToPayResponseJson: JsObject = Json.obj(
    "journeyId" -> "592d4a09cdc8e04b00021459",
    "nextUrl" -> "http://localhost:1111/test"
  )

  val directDebitEnrolled: DirectDebitStatus = DirectDebitStatus(directDebitMandateFound = true, None)
  val directDebitNotEnrolled : DirectDebitStatus = DirectDebitStatus(directDebitMandateFound = false, None)

  val sampleStandingRequest = StandingRequest(
      processingDate = "2024-07-15T09:30:47Z",
      standingRequests = List(
        StandingRequestDetail(
          requestNumber = "20000037272",
          requestCategory = "3",
          createdOn = "2023-11-30",
          changedOn = Some("2024-12-26"),
          requestItems = List(
            RequestItem("1", "24A1", "2024-02-01", "2024-04-30", "2024-03-31", 22945.23, Some("XD006411191344"), Some("2024-03-31")),
            RequestItem("2", "24A1", "2024-02-01", "2024-04-30", "2024-04-30", 22945.23, Some("XD006411191345"), Some("2024-04-30"))
          )
        ),
        StandingRequestDetail(
          requestNumber = "20000037277",
          requestCategory = "3",
          createdOn = "2024-11-30",
          changedOn = Some("2025-01-26"),
          requestItems = List(
            RequestItem("1", "25A1", "2025-02-01", "2025-04-30", "2025-03-31", 122945.23, None, None),
            RequestItem("2", "25A1", "2025-02-01", "2025-04-30", "2025-04-30", 122945.23, None, None)
          )
        )
      )
  )

  val sampleStandingRequestResponse = sampleStandingRequest

  val standingRequestSample2 = StandingRequest(
    "2025-03-15", List(
      StandingRequestDetail(
        requestNumber = "20000037272",
        requestCategory = "3",
        createdOn = "2025-03-15",
        changedOn = Some("2025-03-15"),
        requestItems = List(
          RequestItem(
            period = "1",
            periodKey = "25A1",
            startDate = "2025-01-01",
            endDate = "2025-02-02",
            dueDate = "2025-02-02",
            amount = 25000.50,
            chargeReference = Some("XD006411191344"),
            postingDueDate = None
          ),
          RequestItem(
            period = "2",
            periodKey = "25A1",
            startDate = ("2025-02-01"),
            endDate = ("2025-03-31"),
            dueDate = ("2025-03-31"),
            amount = 20000.75,
            chargeReference = Some("XD006411191345"),
            postingDueDate = Some("2024-04-30")
          )
        )
      ),
      StandingRequestDetail(
        requestNumber = "20000037273",
        requestCategory = "2",
        createdOn = "2023-11-30",
        changedOn = Some("2025-02-01"),
        requestItems = List(
          RequestItem(
            period = "1",
            periodKey = "25A1",
            startDate = ("2025-04-01"),
            endDate = ("2025-06-30"),
            dueDate = ("2025-06-30"),
            amount = 22945.23,
            chargeReference = Some("XD006411191344"),
            postingDueDate = Some("2025-06-30")
          )
        )
      )
    )
  )

}
