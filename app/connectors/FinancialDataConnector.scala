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

package connectors

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import connectors.httpParsers.PaymentsHttpParser.HttpGetResult
import models.{Payment, Payments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient) {

  def getPaymentsForVatReturns(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Payments]] = {
    // TODO: This needs replacing with a real call to the financial data api once it becomes available
    Future.successful(Right(
      Payments(
        Seq(
          Payment(
            end = LocalDate.parse("2017-01-01"),
            due = LocalDate.parse("2017-10-25"),
            outstandingAmount = BigDecimal(1000.00),
            status = "O",
            periodKey = "#003"
          ),
          Payment(
            end = LocalDate.parse("2017-10-19"),
            due = LocalDate.parse("2017-12-25"),
            outstandingAmount = BigDecimal(10.00),
            status = "O",
            periodKey = "#001"
          )
        )
      )
    ))
  }

}
