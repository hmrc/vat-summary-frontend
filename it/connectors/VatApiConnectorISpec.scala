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
import java.time.temporal.ChronoUnit

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.{VatReturn, VatReturns}
import models.obligations.Obligation.Status
import models.errors.{BadRequestError, MultipleErrors}
import stubs.VatApiStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatApiConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: VatApiConnector = app.injector.instanceOf[VatApiConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getObligations with a status of 'A'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubAllObligations

      val expected = Right(VatReturns(
        Seq(
          VatReturn(
            start = LocalDate.now().minus(80L, ChronoUnit.DAYS),
            end = LocalDate.now().minus(50L, ChronoUnit.DAYS),
            due = LocalDate.now().minus(40L, ChronoUnit.DAYS),
            status = "F",
            received = Some(LocalDate.now().minus(45L, ChronoUnit.DAYS)),
            periodKey = "#001"
          ),
          VatReturn(
            start = LocalDate.now().minus(70L, ChronoUnit.DAYS),
            end = LocalDate.now().minus(40L, ChronoUnit.DAYS),
            due = LocalDate.now().minus(30L, ChronoUnit.DAYS),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getObligations("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.All))

      result shouldEqual expected
    }

  }

  "calling getObligations with a status of 'O'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubOutstandingObligations

      val expected = Right(VatReturns(
        Seq(
          VatReturn(
            start = LocalDate.now().minus(70L, ChronoUnit.DAYS),
            end = LocalDate.now().minus(40L, ChronoUnit.DAYS),
            due = LocalDate.now().minus(30L, ChronoUnit.DAYS),
            status = "O",
            received = None,
            periodKey = "#004"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getObligations("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getObligations with a status of 'F'" should {

    "return all obligations for a given period" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubFulfilledObligations

      val expected = Right(VatReturns(
        Seq(
          VatReturn(
            start = LocalDate.now().minus(80L, ChronoUnit.DAYS),
            end = LocalDate.now().minus(50L, ChronoUnit.DAYS),
            due = LocalDate.now().minus(40L, ChronoUnit.DAYS),
            status = "F",
            received = Some(LocalDate.now().minus(45L, ChronoUnit.DAYS)),
            periodKey = "#001"
          )
        )
      ))

      setupStubs()
      private val result = await(connector.getObligations("123456789",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getObligations with an invalid VRN" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getObligations with an invalid 'from' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidFromDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_FROM",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getObligations with an invalid 'to' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidToDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_TO",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getObligations with an invalid date range" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidDateRange

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_RANGE",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-12-31"),
        LocalDate.parse("2017-01-01"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getObligations with an invalid obligation status" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidStatus

      val expected = Left(BadRequestError(
        code = "INVALID_STATUS",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

  "calling getObligations with multiple errors" should {

    "return an MultipleErrors" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubMultipleErrors

      val expected = Left(MultipleErrors)

      setupStubs()
      private val result = await(connector.getObligations("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Fulfilled))

      result shouldEqual expected
    }

  }

}
