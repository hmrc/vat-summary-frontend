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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.DirectDebitDetailsModel
import models.errors.UnexpectedStatusError
import stubs.DirectDebitStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class DirectDebitConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: DirectDebitConnector = app.injector.instanceOf[DirectDebitConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling setupJourney valid data" should {

    "return redirect URL" in new Test {
      override def setupStubs(): StubMapping = DirectDebitStub.stubDirectDebitJourneyInfo

      val expected = Right("http://www.google.com")

      setupStubs()
      private val result = await(connector.setupJourney(DirectDebitDetailsModel("", "", "", "")))

      result shouldEqual expected
    }

    "return an error" in new Test {
      override def setupStubs(): StubMapping = DirectDebitStub.stubErrorFromApi

      val expected = Left(UnexpectedStatusError("500", "blah"))

      setupStubs()
      private val result = await(connector.setupJourney(DirectDebitDetailsModel("", "", "", "")))

      result shouldEqual expected
    }
  }
}
