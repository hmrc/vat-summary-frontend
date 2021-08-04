/*
 * Copyright 2021 HM Revenue & Customs
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
import models.payments.{ChargeType, PaymentDetailsModel, ReturnDebitCharge}
import stubs.PaymentsStub
import uk.gov.hmrc.http.HeaderCarrier
import play.api.test.Helpers._

class PaymentsConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: PaymentsConnector = app.injector.instanceOf[PaymentsConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling setupJourney valid data" should {

    val testChargeType: ChargeType = ReturnDebitCharge

    "return redirect URL" in new Test {
      override def setupStubs(): StubMapping = PaymentsStub.stubPaymentsJourneyInfo(testChargeType.toPathElement)

      val expected = Right("http://www.google.com")

      setupStubs()
      private val result = await(connector.setupJourney(
        PaymentDetailsModel(
          taxType = "",
          taxReference = "",
          amountInPence = 0,
          taxPeriodMonth = 0,
          taxPeriodYear = 0,
          vatPeriodEnding = "1970-01-01",
          returnUrl = "",
          backUrl = "",
          chargeType = testChargeType,
          dueDate = "",
          chargeReference = None
        )
      ))

      result shouldEqual expected
    }

    "return an error" in new Test {
      override def setupStubs(): StubMapping = PaymentsStub.stubErrorFromApi(testChargeType.toPathElement)

      val expected = Left(UnexpectedStatusError("500", "blah"))

      setupStubs()
      private val result = await(connector.setupJourney(
        PaymentDetailsModel(
          "",
          "",
          0,
          0,
          0,
          "1970-01-01",
          "",
          "",
          testChargeType,
          "",
          None
        )
      ))

      result shouldEqual expected
    }
  }
}
