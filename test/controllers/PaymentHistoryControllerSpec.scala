/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import java.time.LocalDate
import common.SessionKeys
import common.TestModels._
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.{UnknownError, VatLiabilitiesError}
import models.payments.ReturnDebitCharge
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import models.{CustomerInformation, ServiceResponse}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.payments.PaymentHistory
import play.api.test.Helpers.defaultAwaitTimeout
import scala.concurrent.{ExecutionContext, Future}

class PaymentHistoryControllerSpec extends ControllerBaseSpec {

  val paymentHistory: PaymentHistory = injector.instanceOf[PaymentHistory]
  val exampleAmount: Int = 100
  val emptyResult: ServiceResponse[Seq[PaymentsHistoryModel]] = Right(Seq())
  val currentYear: Int = 2018
  val accountDetailsResponseNoMigratedDate: HttpGetResult[CustomerInformation] = Right(customerInformationMin)

  val controller = new PaymentHistoryController(
    mockPaymentsService,
    authorisedController,
    mockDateService,
    mockServiceInfoService,
    mockAccountDetailsService,
    mockServiceErrorHandler,
    mcc,
    paymentHistory,
    ddInterruptPredicate,
    mockAuditService
  )

  val serviceResultYearOne: ServiceResponse[Seq[PaymentsHistoryModel]] =
    Right(Seq(
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-02-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-03-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-03-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-04-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-05-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2017-03-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-04-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-05-01"))
      )
    ))
  val serviceResultYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]] =
    Right(Seq(
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2017-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2017-02-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2017-03-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2017-03-01")),
        taxPeriodTo = Some(LocalDate.parse("2017-04-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2017-05-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2017-03-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-04-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-05-01"))
      )
    ))
  val serviceResultYearThree: ServiceResponse[Seq[PaymentsHistoryModel]] =
    Right(Seq(
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2016-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2016-02-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2016-03-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2016-07-01")),
        taxPeriodTo = Some(LocalDate.parse("2016-08-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2016-09-01"))
      ),
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.parse("2016-09-01")),
        taxPeriodTo = Some(LocalDate.parse("2016-10-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2016-11-01"))
      )
    ))

  def mockPaymentHistory(paymentHistoryResponse: ServiceResponse[Seq[PaymentsHistoryModel]]): Any =
    (mockPaymentsService.getPaymentsHistory(_: String, _: Int)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *).noMoreThanOnce()
      .returns(Future.successful(paymentHistoryResponse))

  "Calling the paymentHistory action" when {

    "the user is logged in" when {

      lazy val result = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockPaymentHistory(serviceResultYearOne)
        mockPaymentHistory(serviceResultYearTwo)
        mockCustomerInfo(Right(customerInformationMax))
        controller.paymentHistory()(fakeRequestWithSession)
      }

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in {
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.paymentHistory()(fakeRequestWithSession)
    }

      "return SEE_OTHER" in {
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {

      lazy val result = {
        mockInsufficientEnrolments()
        controller.paymentHistory()(fakeRequestWithSession)
    }

      "return 403 (Forbidden)" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is an Agent" should {

      lazy val result = {
        mockAgentAuth()
        mockDateServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockPaymentHistory(serviceResultYearOne)
        mockPaymentHistory(serviceResultYearTwo)
        mockCustomerInfo(Right(customerInformationMax))
        controller.paymentHistory()(agentFinancialRequest)
      }
      "return 200" in {
        status(result) shouldBe Status.OK
      }
    }

    "the user is hybrid" should {

      lazy val result = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationHybrid))
        controller.paymentHistory()(fakeRequest.withSession(SessionKeys.agentSessionVrn -> "123456789"))
      }

      "redirect to VAT overview page" in {
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details().url)
      }
    }

    "the call to retrieve hybrid status fails" should {

      lazy val result = {
        mockPrincipalAuth()
        mockCustomerInfo(Left(UnknownError))
        controller.paymentHistory()(fakeRequest)
      }

      "return Internal Server Error" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "an error occurs upstream" should {

      lazy val result = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockCustomerInfo(Right(customerInformationMax))
        mockPaymentHistory(Left(VatLiabilitiesError))
        mockPaymentHistory(Left(VatLiabilitiesError))
        controller.paymentHistory()(fakeRequestWithSession)
      }
      "return Internal Server Error" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

    "return the standard error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
      }
    }
  }

    "the user is insolvent and not continuing to trade" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.paymentHistory()(insolventRequest)
      }
      "return 403 (Forbidden)" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.paymentHistory()(DDInterruptRequest)
      }
      "return 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }

    "the user has a VATDEC enrolment and no customerMigratedToETMPDate" should {

      lazy val result = {
        mockVatDec()
        mockDateServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockPaymentHistory(serviceResultYearOne)
        mockPaymentHistory(serviceResultYearTwo)
        mockPaymentHistory(serviceResultYearThree)
        mockCustomerInfo(accountDetailsResponseNoMigratedDate)
        mockCustomerInfo(accountDetailsResponseNoMigratedDate)
        controller.paymentHistory()(fakeRequest)
      }
      "return 200" in {
        status(result) shouldBe OK
      }

      "render the Previous Payments tab" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("li.govuk-tabs__list-item:nth-child(4)").text() shouldBe "Previous payments"
      }
    }

  "Calling .customerMigratedWithin15M" when {

    "the interval between dates is less than 15 months" should {

      "return true" in {
        mockDateServiceCall()
        controller.customerMigratedWithin15M(Some(LocalDate.parse("2017-02-02"))) shouldBe true
      }
    }

    "the interval between dates is 15 months or greater" should {

      "return false" in {
        mockDateServiceCall()
        controller.customerMigratedWithin15M(Some(LocalDate.parse("2017-02-01"))) shouldBe false
      }
    }

    "the interval is 0 days" should {

      "return true" in {
        mockDateServiceCall()
        controller.customerMigratedWithin15M(Some(LocalDate.parse("2018-05-01"))) shouldBe true
      }
    }

    "the date provided is None" should {

      "return false as a default" in {
        controller.customerMigratedWithin15M(None) shouldBe false
      }
    }
  }

  "Calling .getMigratedToETMPDate" when {

    "the account details service response contains customer info" should {

      "return the date" in {
        controller.getMigratedToETMPDate(Right(customerInformationMax)) shouldBe Some(LocalDate.parse("2017-05-06"))
      }
    }

    "the account details service response contains an error" should {

      "return None" in {
        controller.getMigratedToETMPDate(Left(UnknownError)) shouldBe None
      }
    }
  }

  "Calling .getHybridToFullMigrationDate" when {

    "the account details service response contains customer info" when {

      "the user has a hybridToFullMigrationDate" should {

        "return the date" in {
          controller.getHybridToFullMigrationDate(Right(customerInformationMax)) shouldBe Some(LocalDate.parse("2017-05-06"))
        }
      }

      "the user has no hybridToFullMigrationDate" should {

        "return None" in {
          controller.getHybridToFullMigrationDate(Right(customerInformationMax.copy(hybridToFullMigrationDate = None))) shouldBe None
        }
      }
    }

    "the account details service response contains an error" should {

      "return None" in {
        controller.getHybridToFullMigrationDate(Left(UnknownError)) shouldBe None
      }
    }
  }

  "Calling .showInsolventContent" when {

    "the account details service response contains customer info" when {

      "the user is insolvent and not exempt from restrictions" should {

        "return true" in {
          controller.showInsolventContent(Right(customerInformationInsolventTrading)) shouldBe true
        }
      }

      "the user is insolvent and exempt from restrictions" should {

        "return false" in {
          controller.showInsolventContent(Right(customerInformationInsolventTradingExempt)) shouldBe false
        }
      }

      "the user is not insolvent" should {

        "return false" in {
          controller.showInsolventContent(Right(customerInformationMax)) shouldBe false
        }
      }
    }

    "the account details service response contains an error" should {

      "return false" in {
        controller.showInsolventContent(Left(UnknownError)) shouldBe false
      }
    }
  }

  "Calling .getValidYears" when {

    "the migration year is equal to the current year" should {

      "return a sequence of just the current year" in {
        mockDateServiceCall()
        controller.getValidYears(Some(LocalDate.parse("2018-01-01"))) shouldBe Seq(currentYear)
      }
    }

    "the migration year is the previous year" should {

      "return a sequence of the current year and previous year" in {
        mockDateServiceCall()
        controller.getValidYears(Some(LocalDate.parse("2017-12-12"))) shouldBe Seq(currentYear, currentYear - 1)
      }
    }

    "the migration year is two years ago" should {

      "return a sequence of the current year and the two years prior" in {
        mockDateServiceCall()
        controller.getValidYears(Some(LocalDate.parse("2016-12-12"))) shouldBe Seq(currentYear, currentYear - 1, currentYear - 2)
      }
    }

    "the migration year could not be retrieved" should {

      "return a sequence of the current year and previous year" in {
        mockDateServiceCall()
        controller.getValidYears(None) shouldBe Seq(currentYear, currentYear - 1, currentYear - 2)
      }
    }
  }

  "Calling .isLast24Months" should {

    "return true" when {

      "the provided date is younger than 24 months" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2016, 6, 1)
        controller.isLast24Months(Some(LocalDate.of(year, month, day))) shouldBe true
      }
      "the provided date is exactly 24 months ago" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2016, 5, 1)
        controller.isLast24Months(Some(LocalDate.of(year, month, day))) shouldBe true
      }
      "no date is provided" in {
        mockDateServiceCall()
        controller.isLast24Months(None) shouldBe true
      }
    }

    "return false" when {

      "the provided date is older than 24 months" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2016, 4, 1)
        controller.isLast24Months(Some(LocalDate.of(year, month, day))) shouldBe false
      }
    }
  }

  "Calling .generateViewModel" when {

    "both service call parameters are successful" when {

      "the customer was migrated in the current year" should {

        "return a PaymentsHistoryViewModel with the correct information" in {
          mockDateServiceCall()
          controller.generateViewModel(
          serviceResultYearOne,
          serviceResultYearTwo,
          emptyResult,
          showPreviousPaymentsTab = false,
          Some(LocalDate.parse("2018-01-01")),
          showInsolvencyContent = false,
          None
          ) shouldBe Some(PaymentsHistoryViewModel(
          currentYear,
          None,
          None,
          previousPaymentsTab = false,
          (serviceResultYearOne.right.get ++ serviceResultYearTwo.right.get).distinct,
          showInsolvencyContent = false,
          None
          ))
        }
      }

      "the customer was migrated in the previous year" should {

        "return a PaymentsHistoryViewModel with the correct information" in {
          mockDateServiceCall()
          controller.generateViewModel(
          serviceResultYearOne,
          serviceResultYearTwo,
          emptyResult,
          showPreviousPaymentsTab = false,
          Some(LocalDate.parse("2017-01-01")),
          showInsolvencyContent = false,
          None
          ) shouldBe Some(PaymentsHistoryViewModel(
          currentYear,
          Some(currentYear - 1),
          None,
          previousPaymentsTab = false,
          (serviceResultYearOne.right.get ++ serviceResultYearTwo.right.get).distinct,
          showInsolvencyContent = false,
          None
          ))
        }
      }

      "the customer was migrated two years ago" should {

        "return a PaymentsHistoryViewModel with the correct information" in {
          mockDateServiceCall()
          controller.generateViewModel(
          serviceResultYearOne,
          serviceResultYearTwo,
          serviceResultYearThree,
          showPreviousPaymentsTab = false,
          Some(LocalDate.parse("2016-12-12")),
          showInsolvencyContent = false,
          None
          ) shouldBe Some(PaymentsHistoryViewModel(
          currentYear,
          Some(currentYear - 1),
          Some(currentYear - 2),
          previousPaymentsTab = false,
          (serviceResultYearOne.right.get ++ serviceResultYearTwo.right.get ++ serviceResultYearThree.right.get.drop(1)).distinct,
          showInsolvencyContent = false,
          None
          ))
        }
      }
    }

    "either of the service call parameters fail" should {

      "return None" in {
        val serviceResultYearOne = Left(VatLiabilitiesError)
        controller.generateViewModel(
        serviceResultYearOne,
        serviceResultYearTwo,
        serviceResultYearThree,
        showPreviousPaymentsTab = false,
        None,
        showInsolvencyContent = false,
        None
        ) shouldBe None
      }
    }
  }

  "Calling .checkIfMigrationWithinLastThreeYears" when {

    "return true" when {

      "the provided date is less than 3 years ago" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2016, 6, 1)
        controller.checkIfMigrationWithinLastThreeYears(Some(LocalDate.of(year, month, day))) shouldBe true
      }
    }

    "return false" when {

      "the provided date is more than 3 years ago" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2014, 4, 1)
        controller.checkIfMigrationWithinLastThreeYears(Some(LocalDate.of(year, month, day))) shouldBe false
      }

      "the provided date is exactly 3 years ago" in {
        mockDateServiceCall()
        val (year, month, day): (Int, Int, Int) = (2015, 5, 1)
        controller.checkIfMigrationWithinLastThreeYears(Some(LocalDate.of(year, month, day))) shouldBe false
      }

      "no date is provided" in {
        controller.checkIfMigrationWithinLastThreeYears(None) shouldBe false
      }
    }
  }
}
