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

import models.WYODatabaseModel
import models.viewModels._
import play.api.libs.json.Json
import repositories.WYOSessionRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WYOSessionService @Inject()(repository: WYOSessionRepository) {

  def storeChargeModels(viewModels: Seq[ChargeDetailsViewModel], vrn: String)
                       (implicit ec: ExecutionContext): Future[Seq[Boolean]] = {

    val results: Seq[Future[Boolean]] = viewModels.map { model =>

      val json = model match {
        case m: StandardChargeViewModel => Json.toJson(m)
        case m: EstimatedInterestViewModel => Json.toJson(m)
        case m: EstimatedLPP1ViewModel => Json.toJson(m)
        case m: EstimatedLPP2ViewModel => Json.toJson(m)
        case m: CrystallisedInterestViewModel => Json.toJson(m)
        case m: CrystallisedLPP1ViewModel => Json.toJson(m)
        case m: CrystallisedLPP2ViewModel => Json.toJson(m)
        case m: VatOverpaymentForRPIViewModel => Json.toJson(m)
        case m: LateSubmissionPenaltyViewModel => Json.toJson(m)
      }
      val databaseModel = WYODatabaseModel(model.generateHash(vrn), model.getClass.getSimpleName, json)

      repository.write(databaseModel)
    }

    Future.sequence(results)
  }

  def retrieveViewModel(id: String): Future[Option[WYODatabaseModel]] =
    repository.read(id)
}
