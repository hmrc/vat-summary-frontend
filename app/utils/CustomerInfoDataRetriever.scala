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

package utils

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.CustomerInformation
import java.time.LocalDate
import play.api.Logger

object CustomerInfoDataRetriever {

  def retrieveCustomerMigratedToETMPDate(accountDetails: HttpGetResult[CustomerInformation]): Option[LocalDate] =
    accountDetails match {
      case Right(model) =>
        model.customerMigratedToETMPDate.fold[Option[LocalDate]](None) { dateString =>
          if (!dateString.isEmpty) {
            Some(LocalDate.parse(dateString))
          }
          else {
            None
          }
        }

      case Left(error) =>
        Logger.warn("[CustomerInfoDataRetriever][retrieveCustomerMigratedToETMPDate] " +
          "could not convert customer info to model to get customer migrated to ETMP date: " + error.toString)
        None
    }
}
