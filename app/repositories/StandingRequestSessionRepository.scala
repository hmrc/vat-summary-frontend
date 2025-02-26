/*
 * Copyright 2025 HM Revenue & Customs
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

package repositories

import config.AppConfig
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.{IndexModel, IndexOptions, ReplaceOptions}
import org.mongodb.scala.model.Indexes.ascending
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import models.StandingRequestDatabaseModel

@Singleton
class StandingRequestSessionRepository @Inject()(appConfig: AppConfig, mongo: MongoComponent)
                                               (implicit ec: ExecutionContext)
  extends PlayMongoRepository[StandingRequestDatabaseModel](
    collectionName = "standingRequestSessionData",
    mongoComponent = mongo,
    domainFormat = StandingRequestDatabaseModel.format,
    indexes = Seq(
      IndexModel(
        ascending("creationTimestamp"),
        IndexOptions()
          .name("expiry")
          .expireAfter(appConfig.timeToLiveInSeconds, TimeUnit.SECONDS)
      )
    )
  ) {

  def write(sessionId: String, standingRequest: StandingRequestDatabaseModel): Future[Boolean] = {
    collection.replaceOne(
      equal("_id", sessionId), 
      standingRequest, 
      ReplaceOptions().upsert(true)
    ).map(_.wasAcknowledged()).head()
  }

  def read(sessionId: String): Future[Option[StandingRequestDatabaseModel]] = {
    collection.find(equal("_id", sessionId)).headOption()
  }
}