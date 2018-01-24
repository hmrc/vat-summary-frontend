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

import javax.inject.Inject

import config.AppConfig
import connectors.httpParsers.CustomerInfoHttpParser.HttpGetResult
import models.CustomerInformation
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class CustomerInformationConnector @Inject()(http: HttpClient, appConfig: AppConfig) {

  import connectors.httpParsers.CustomerInfoHttpParser.CustomerInfoReads

  private[connectors] def customerInformationUrl(vrn: String): String = {
    s"${appConfig.customerInformationBaseUrl}/customer-information/vat/$vrn"
  }

  def getCustomerInfo(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext)
  : Future[HttpGetResult[CustomerInformation]] = {
    http.GET(customerInformationUrl(vrn))
      .map {
        case customerInfo@Right(_) => customerInfo
        case httpError@Left(error) =>
          Logger.info("CustomerInformationConnector received error: " + error.message)
          httpError
      }
  }

}
