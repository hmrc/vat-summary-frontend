/*
 * Copyright 2017 HM Revenue & Customs
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

import models.{Payment, Payments}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.Future

@Singleton
class FinancialDataConnector  @Inject()(http: HttpClient) {

  def getPaymentData(vrn: String): Future[Payments] = {
    Future.successful(
      Payments(
        Seq(
          Payment(
            endDate = LocalDate.parse("2017-01-01"),
            dueDate = LocalDate.parse("2017-10-25"),
            outstandingAmount = BigDecimal(1000.00),
            status = "O",
            periodKey = "#003"
          ),
          Payment
          (
            endDate = LocalDate.parse("2017-10-19"),
            dueDate = LocalDate.parse("2017-12-25"),
            outstandingAmount = BigDecimal(10.00),
            status = "O",
            periodKey = "#001"
          )
        )
      )
    )
  }

}
