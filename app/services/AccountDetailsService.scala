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

package services

import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}

import models.errors.CustomerInformationError
import models.{CustomerInformation, ServiceResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccountDetailsService @Inject()(connector: VatSubscriptionConnector) {

  def getAccountDetails(vrn: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CustomerInformation]] =
    connector.getCustomerInfo(vrn)


  def getEntityName(vrn: String) (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Option[String]]] =
    connector.getCustomerInfo(vrn).map {
      case Right(model: CustomerInformation) => Right(model.entityName)
      case Left(_) => Left(CustomerInformationError)
    }
}
