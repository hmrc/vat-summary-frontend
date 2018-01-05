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

package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import connectors.httpParsers.ObligationsHttpParser._
import helpers.IntegrationBaseSpec
import models.errors.BadRequestError
import models.payments.Payment
import models.{VatReturn, User, VatDetailsModel}
import stubs.VatApiStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatDetailsServiceISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    implicit val hc: HeaderCarrier = HeaderCarrier()
    lazy val service: VatDetailsService = app.injector.instanceOf[VatDetailsService]
    setupStubs()
  }

  "Calling getVatDetails" when {

    "the user has outstanding obligations" should {

      "return the user's latest obligation" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubOutstandingObligations

        val obligation = VatReturn(
          start = LocalDate.now().minus(70L, ChronoUnit.DAYS),
          end = LocalDate.now().minus(40L, ChronoUnit.DAYS),
          due = LocalDate.now().minus(30L, ChronoUnit.DAYS),
          status = "O",
          received = None,
          periodKey = "#004"
        )

        val payment = Payment(
          LocalDate.parse("2017-10-19"),
          LocalDate.parse("2017-12-25"),
          BigDecimal(10.0),
          "O",
          "#001")

        val expected = Right(VatDetailsModel(Some(obligation), Some(payment)))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

    "the user has no outstanding obligations" should {

      "return nothing" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubNoObligations

        val expected = Right(VatDetailsModel(None, Some(Payment(
          LocalDate.parse("2017-10-19"),
          LocalDate.parse("2017-12-25"),
          BigDecimal(10.0),
          "O",
          "#001"))))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

    "the user has an invalid VRN" should {

      "return a BadRequestError" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn

        val expected = Left(BadRequestError("VRN_INVALID", ""))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

  }

}
