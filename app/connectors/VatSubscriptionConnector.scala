/*
 * Copyright 2019 HM Revenue & Customs
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

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models.{CustomerInformation, MandationStatus}
import play.api.Logger
import services.MetricsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(http: HttpClient,
                                         appConfig: AppConfig,
                                         metrics: MetricsService) {

  private[connectors] def customerInfoUrl(vrn: String): String = s"${appConfig.vatSubscriptionBaseUrl}/vat-subscription/$vrn/customer-details"
  private[connectors] def customerMandationUrl(vrn: String): String = s"${appConfig.vatSubscriptionBaseUrl}/vat-subscription/$vrn/mandation-status"

  def getCustomerInfo(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CustomerInformation]] = {

    import connectors.httpParsers.CustomerInfoHttpParser.CustomerInfoReads

    val timer = metrics.getCustomerInfoTimer.time()

    http.GET(customerInfoUrl(vrn))
      .map {
        case customerInfo@Right(_) =>
          timer.stop()
          customerInfo
        case httpError@Left(error) =>
          metrics.getCustomerInfoCallFailureCounter.inc()
          Logger.warn("CustomerInformationConnector received error: " + error.message)
          httpError
      }
  }

  def getCustomerMandationStatus(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[MandationStatus]] = {

    import connectors.httpParsers.MandationStatusHttpParser.MandationStatusReads

    http.GET(customerMandationUrl(vrn))
      .map {
        case mandationStatus@Right(_) =>
          mandationStatus
        case httpError@Left(error) =>
          Logger.warn("[VatSubscriptionConnector][getCustomerMandationStatus] Error received: " + error)
          httpError
      }
  }
}
