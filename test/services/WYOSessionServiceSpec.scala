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

import common.TestModels._
import models.WYODatabaseModel
import models.viewModels.ChargeDetailsViewModel
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.WYOSessionRepository

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class WYOSessionServiceSpec extends AnyWordSpecLike with Matchers with MockitoSugar with GuiceOneAppPerSuite {

  val mockRepo: WYOSessionRepository = mock[WYOSessionRepository]
  val service = new WYOSessionService(mockRepo)
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  def mockRepoWrite(result: Boolean): Any = when(mockRepo.write(any())).thenReturn(Future.successful(result))
  def mockRepoRead(result: Option[WYODatabaseModel]): Any = when(mockRepo.read(any())).thenReturn(Future.successful(result))

  "The storeChargeModels function" should {

    "take a sequence of ChargeDetailsViewModel and write an entry to the database for each one" in {
      mockRepoWrite(true)
      val charges =
        Seq(chargeModel1, estimatedInterestModel, estimatedLPP1Model, estimatedLPP2Model, crystallisedInterestCharge,
          crystallisedLPP1Model, crystallisedLPP2Model, lateSubmissionPenaltyModel)
      val result = service.storeChargeModels(charges, "999999999")

      await(result) shouldBe Seq.fill(charges.length)(true)
    }

    "throw an exception when an unsupported view model is provided" in {
      val unsupportedModel = new ChargeDetailsViewModel {
        val chargeType: String = "unsupportedChargeType"
        override val outstandingAmount: BigDecimal = 0
        override def description(isAgent: Boolean)(implicit messages: play.api.i18n.Messages): String = "description"
      }

      intercept[MatchError](service.storeChargeModels(Seq(unsupportedModel), "999999999"))
    }
  }

  "The retrieveViewModel function" should {

    "return the result from the database's read operation" in {
      val databaseModel = WYODatabaseModel("abc", "StandardChargeViewModel", Json.obj(), LocalDateTime.now())
      mockRepoRead(Some(databaseModel))
      await(service.retrieveViewModel("abc")) shouldBe Some(databaseModel)
    }
  }
}
