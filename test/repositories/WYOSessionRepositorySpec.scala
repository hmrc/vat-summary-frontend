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

package repositories

import config.AppConfig
import mocks.MockAppConfig
import models.WYODatabaseModel
import org.mongodb.scala.bson.{BsonInt64, BsonString}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import java.time.Instant

import scala.concurrent.ExecutionContext

class WYOSessionRepositorySpec extends AnyWordSpecLike with Matchers with GuiceOneAppPerSuite with
  DefaultPlayMongoRepositorySupport[WYODatabaseModel] {

  lazy val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  override lazy val repository = new WYOSessionRepository(mockAppConfig, mongoComponent)

  val time: Instant = Instant.parse("2022-01-01T09:00:00.00Z")
  val exampleJson: JsObject = Json.obj("chargeType" -> "VAT Return Debit Charge", "periodKey" -> "18AA")
  val exampleModel: WYODatabaseModel = WYODatabaseModel("abc", "StandardChargeViewModel", exampleJson, time)

  "The WYOSessionRepository" should {

    "have a TTL index on the creationTimestamp field, with an expiry time set by AppConfig" in {
      val indexes = {
        await(repository.ensureIndexes())
        await(repository.collection.listIndexes().toFuture())
      }
      val ttlIndex = indexes.find(_.get("name").contains(BsonString("expiry")))

      ttlIndex.get("key").toString shouldBe """{"creationTimestamp": 1}"""
      ttlIndex.get("expireAfterSeconds") shouldBe BsonInt64(mockAppConfig.timeToLiveInSeconds)
    }
  }

  "Writing to the WYOSessionRepository" when {

    "there is no document with the given ID" should {

      "write the document to the database" in {
        await(repository.write(exampleModel)) shouldBe true
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
      }
    }

    "there is an existing document with the same ID" should {

      "replace the existing document in the database" in {
        await(repository.write(exampleModel))
        await(repository.write(exampleModel.copy(creationTimestamp = time.plusSeconds(60)))) shouldBe true
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
      }
    }
  }

  "Reading from the WYOSessionRepository" when {

    "a document is found for the given ID" should {

      "return a WYODatabaseModel made up of data from the document" in {
        await(repository.write(exampleModel))
        await(repository.read("abc")) shouldBe Some(exampleModel)
      }
    }

    "no document is found for the given ID" should {

      "return None" in {
        await(repository.read("abc")) shouldBe None
      }
    }
  }
}
