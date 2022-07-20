/*
 * Copyright 2022 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.errors.UnexpectedStatusError
import uk.gov.hmrc.http.HeaderCarrier
import stubs.PenaltyDetailsStub
import stubs.PenaltyDetailsStub._
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}

class PenaltyDetailsISpec extends IntegrationBaseSpec{

  val idType = "vatIdType"
  val idValue = "vatIdValue"

  private trait Test {
    def setupStubs(): StubMapping
    val connector: PenaltyDetailsConnector = app.injector.instanceOf[PenaltyDetailsConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPenaltyDetails" should {

    "return a users penaltyDetails information" in new Test {
      override def setupStubs(): StubMapping = PenaltyDetailsStub.stubPenaltyDetails(
        OK, penaltyDetailsJsonMax, idType, idValue
      )

      val expected = Right(penaltyDetailsModelMax)

      setupStubs()
      private val result = await(connector.getPenaltyDetails(idType, idValue))

      result shouldEqual expected
    }

    "return an HttpError if one is received" in new Test {
      override def setupStubs(): StubMapping = PenaltyDetailsStub.stubPenaltyDetails(INTERNAL_SERVER_ERROR, errorJson, idType, idValue)

      val message: String = """{"code":"500","message":"INTERNAL_SERVER_ERROR"}"""
      val expected = Left(UnexpectedStatusError(INTERNAL_SERVER_ERROR.toString,message))

      setupStubs()
      private val result = await(connector.getPenaltyDetails(idType, idValue))

      result shouldEqual expected
    }

  }
}
