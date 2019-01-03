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

package connectors

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.errors.{ApiSingleError, BadRequestError, MultipleErrors}
import models.obligations.Obligation.Status
import models.obligations.{VatReturnObligation, VatReturnObligations}
import play.api.libs.json.Json
import stubs.VatObligationsStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatObligationsConnectorISpec extends IntegrationBaseSpec {

  val obligationsStub: VatObligationsStub = new VatObligationsStub(
    app.configuration.underlying.getBoolean("features.useVatObligationsService.enabled")
  )

  private trait Test {
    def setupStubs(): StubMapping

    val connector: VatObligationsConnector = app.injector.instanceOf[VatObligationsConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getVatReturnObligations with a status of 'A'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubAllObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.parse("2018-01-01"),
            end = LocalDate.parse("2018-03-31"),
            due = LocalDate.parse("2018-05-07"),
            status = "F",
            received = Some(LocalDate.parse("2018-04-15")),
            periodKey = "#001"
          ),
          VatReturnObligation(
            start = LocalDate.parse("2018-01-01"),
            end = LocalDate.parse("2018-03-31"),
            due = LocalDate.parse("2018-05-07"),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-12-31"),
        Status.All))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with a status of 'O'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubOutstandingObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.parse("2018-01-01"),
            end = LocalDate.parse("2018-03-31"),
            due = LocalDate.parse("2018-05-07"),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with a status of 'F'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubFulfilledObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.parse("2018-01-01"),
            end = LocalDate.parse("2018-03-31"),
            due = LocalDate.parse("2018-05-07"),
            status = "F",
            received = Some(LocalDate.parse("2018-04-15")),
            periodKey = "#001"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid VRN" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubInvalidVrn

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid 'from' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubInvalidFromDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_FROM",
        errorResponse = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid 'to' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubInvalidToDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_TO",
        errorResponse = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid date range" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubInvalidDateRange

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_RANGE",
        errorResponse = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-12-31"),
        LocalDate.parse("2017-01-01"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid obligation status" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubInvalidStatus

      val expected = Left(BadRequestError(
        code = "INVALID_STATUS",
        errorResponse = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "Calling getVatReturnObligations with multiple errors" should {

    "return a MultipleErrors" in new Test {
      override def setupStubs(): StubMapping = obligationsStub.stubMultipleErrors

      val errors = Seq(ApiSingleError("ERROR_1", "MESSAGE_1"), ApiSingleError("ERROR_2", "MESSAGE_2"))
      val expected = Left(MultipleErrors("BAD_REQUEST", Json.toJson(errors).toString()))
      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldBe expected
    }
  }
}
