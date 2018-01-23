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

import config.AppConfig
import connectors.httpParsers.VatReturnObligationsHttpParser.HttpGetResult
import models.CustomerInformation
import models.obligations.Obligation.Status
import models.obligations.VatReturnObligations
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatApiConnector @Inject()(http: HttpClient, appConfig: AppConfig) {

  import connectors.httpParsers.VatReturnObligationsHttpParser.VatReturnsReads

  private[connectors] def obligationsUrl(vrn: String): String = s"${appConfig.vatApiBaseUrl}/vat/$vrn/obligations"

  def getVatReturnObligations(vrn: String,
                 from: LocalDate,
                 to: LocalDate,
                 status: Status.Value)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatReturnObligations]] = {
    http.GET(obligationsUrl(vrn), Seq("from" -> from.toString, "to" -> to.toString, "status" -> status.toString))
      .map {
        case vatReturns@Right(_) => vatReturns
        case httpError@Left(error) =>
          Logger.info("VatApiConnector received error: " + error.message)
          httpError
      }
  }

  // TODO: Replace with a real call to an endpoint once it becomes available. This returns static data for now.
  def getCustomerInfo(vrn: String)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CustomerInformation]] = {
    Future.successful(Right(CustomerInformation("Cheapo Clothing Ltd")))
  }
}
