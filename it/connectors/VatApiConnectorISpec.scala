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

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.VatReturnObligation.Status
import models.errors.{BadRequestError, MultipleErrors}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.VatReturnObligation
import stubs.VatApiStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatApiConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: VatApiConnector = app.injector.instanceOf[VatApiConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getVatReturnObligations with a status of 'A'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubAllObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.now().minusDays(80),
            end = LocalDate.now().minusDays(50),
            due = LocalDate.now().minusDays(40),
            status = "F",
            received = Some(LocalDate.now().minusDays(45)),
            periodKey = "#001"
          ),
          VatReturnObligation(
            start = LocalDate.now().minusDays(70),
            end = LocalDate.now().minusDays(40),
            due = LocalDate.now().minusDays(30),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.All))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with a status of 'O'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubOutstandingObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.now().minusDays(70),
            end = LocalDate.now().minusDays(40),
            due = LocalDate.now().minusDays(30),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with a status of 'F'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubFulfilledObligations

      val expected = Right(VatReturnObligations(
        Seq(
          VatReturnObligation(
            start = LocalDate.now().minusDays(80),
            end = LocalDate.now().minusDays(50),
            due = LocalDate.now().minusDays(40),
            status = "F",
            received = Some(LocalDate.now().minusDays(45)),
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
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        message = ""
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
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidFromDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_FROM",
        message = ""
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
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidToDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_TO",
        message = ""
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
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidDateRange

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_RANGE",
        message = ""
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
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidStatus

      val expected = Left(BadRequestError(
        code = "INVALID_STATUS",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with multiple errors" should {

    "return an MultipleErrors" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubMultipleErrors

      val expected = Left(MultipleErrors)

      setupStubs()
      private val result = await(connector.getVatReturnObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

}
