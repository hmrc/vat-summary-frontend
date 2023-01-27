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

import config.AppConfig
import connectors.TimeToPayConnector
import models.errors.TimeToPayRedirectError
import models.{ServiceResponse, TTPRequestModel}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TimeToPayService @Inject()(ttpConnector: TimeToPayConnector) {

  def retrieveRedirectUrl(implicit ec: ExecutionContext, appConfig: AppConfig): Future[ServiceResponse[String]] = {
    val wyoRoute = appConfig.host + controllers.routes.WhatYouOweController.show.url
    val requestModel = TTPRequestModel(wyoRoute, wyoRoute)

    ttpConnector.callApi(requestModel).map {
      case Right(url) => Right(url) // TODO pull this URL out of the response model added in other task
      case Left(_) => Left(TimeToPayRedirectError)
    }
  }
}
