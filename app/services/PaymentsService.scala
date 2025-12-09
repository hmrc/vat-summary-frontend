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

package services

import java.time.LocalDate
import connectors.{FinancialDataConnector, PaymentsConnector}

import javax.inject.{Inject, Singleton}
import models.errors._
import models.payments.{PaymentDetailsModel, PaymentOnAccount, Payments}
import models.viewModels.PaymentsHistoryModel
import models.{DirectDebitStatus, ServiceResponse}
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentsService @Inject()(financialDataConnector: FinancialDataConnector,
                                paymentsConnector: PaymentsConnector) extends LoggerUtil{

  def getOpenPayments(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Option[Payments]]] =
    financialDataConnector.getOpenPayments(vrn).map {
      case Right(payments) =>
        logger.info("[PaymentsService][getOpenPayments] Successfully retrieved open payments for WhatYouOwe")
        val outstandingPayments = payments.financialTransactions
          .filter(_.outstandingAmount > 0)
          .filterNot(_.chargeType equals PaymentOnAccount)
        if(outstandingPayments.nonEmpty) {
          Right(Some(Payments(outstandingPayments.sortBy(_.due.toString).reverse)))
        } else {
          Right(None)
        }
      case Left(_) => Left(PaymentsError)
    }

  def getPaymentsHistory(vrn: String, currentDate: LocalDate, migrationDate: Option[LocalDate])
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Seq[PaymentsHistoryModel]]] = {

    val from: LocalDate = migrationDate match {
      case Some(migDate) if migDate.isAfter(currentDate.minusYears(2)) => migDate
      case _ => currentDate.minusYears(2)
    }
    val to: LocalDate = currentDate

    financialDataConnector.getVatLiabilities(vrn, from, to).map {
      case Right(liabilities) => Right(liabilities.sortBy(_.clearedDate.toString).reverse)
      case Left(_) => Left(VatLiabilitiesError)
    }
  }

  def getLiabilitiesWithDueDate(vrn: String, currentDate: LocalDate, migrationDate: Option[LocalDate])
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Seq[models.viewModels.PaymentHistoryWithDueDate]]] = {
    val from: LocalDate = migrationDate match {
      case Some(migDate) if migDate.isAfter(currentDate.minusYears(2)) => migDate
      case _ => currentDate.minusYears(2)
    }
    val to: LocalDate = currentDate
    financialDataConnector.getVatLiabilitiesWithDueDate(vrn, from, to).map {
      case Right(rows) => Right(rows)
      case Left(_) => Left(VatLiabilitiesError)
    }
  }

  def setupPaymentsJourney(journeyDetails: PaymentDetailsModel)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[String]] = {
    paymentsConnector.setupJourney(journeyDetails).map {
      case Right(url) => Right(url)
      case Left(_) => Left(PaymentSetupError)
    }
  }

  def getDirectDebitStatus(vrn: String)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[DirectDebitStatus]] =
    financialDataConnector.getDirectDebitStatus(vrn) map {
      case Right(directDebitStatus) => Right(directDebitStatus)
      case Left(_) => Left(DirectDebitStatusError)
    }

  def getPaymentsForPeriod(vrn: String, from: LocalDate, to: LocalDate)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Payments]] =
    financialDataConnector.getPaymentsForPeriod(vrn, from, to).map {
      case Right(payments) => Right(payments)
      case Left(_) => Left(PaymentsError)
    }
}
