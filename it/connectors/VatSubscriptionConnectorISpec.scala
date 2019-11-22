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
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import helpers.IntegrationBaseSpec
import models.errors.ServerSideError
import models.{Address, CustomerInformation, MandationStatus, TaxPeriod}
import stubs.CustomerInfoStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatSubscriptionConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: VatSubscriptionConnector = app.injector.instanceOf[VatSubscriptionConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getCustomerInfo" should {

    "return a user's customer information" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubCustomerInfo()

      val expected = Right(CustomerInformation(
        Some("Cheapo Clothing Ltd"),
        Some("Betty"),
        Some("Jones"),
        Some("Cheapo Clothing"),
        Address("Bedrock Quarry", Some("Bedrock"), Some("Graveldon"), None, Some("GV2 4BB")),
        isHybridUser = false,
        Some("2017-05-05"),
        Some("2017-01-01"),
        Some("7"),
        "10410",
        Some("MM"),
        Some(List(
          TaxPeriod(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-01-15")),
          TaxPeriod(LocalDate.parse("2018-01-06"), LocalDate.parse("2018-01-28")))
        ),
        Some(TaxPeriod(LocalDate.parse("2018-01-29"), LocalDate.parse("2018-01-31"))),
        Some("MTDfB Voluntary")
      ))

      setupStubs()
      private val result = await(connector.getCustomerInfo("1111"))

      result shouldEqual expected
    }

    "return an HttpError if one is received" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubErrorFromApi()

      val message: String = """{"code":"500","message":"INTERNAL_SERVER_ERROR"}"""
      val expected = Left(ServerSideError("500", message))

      setupStubs()
      private val result = await(connector.getCustomerInfo("1111"))

      result shouldEqual expected
    }
  }

  "calling getCustomerMandationStatus" should {

    "return a user's mandation status" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubCustomerMandationStatus()

      val expected = Right(
        MandationStatus(
          "MTDfB Mandated"
        )
      )

      setupStubs()
      private val result: HttpGetResult[MandationStatus] = await(connector.getCustomerMandationStatus("1111"))

      result shouldEqual expected
    }

    "return an HttpError if one is received" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubErrorFromApi("/vat-subscription/([0-9]+)/mandation-status")

      val message: String = """{"code":"500","message":"INTERNAL_SERVER_ERROR"}"""
      val expected = Left(ServerSideError("500", message))

      setupStubs()
      private val result = await(connector.getCustomerMandationStatus("1111"))

      result shouldEqual expected
    }
  }
}
