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

package services

import common.TestModels.customerInformationMax
import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.{BadRequestError, CustomerInformationError}
import models.{CustomerInformation, ServiceResponse}
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier
import org.scalatest.matchers.should.Matchers
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AccountDetailsServiceSpec extends AnyWordSpecLike with MockFactory with Matchers {

  val customerInfoResult: HttpGetResult[CustomerInformation] = Right(customerInformationMax)

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def setup(customerInfoResult: HttpGetResult[CustomerInformation]): Any = {
    (mockVatSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(customerInfoResult))
  }

  def accountDetailsService(customerInfoResult: HttpGetResult[CustomerInformation] = customerInfoResult): AccountDetailsService = {
    setup(customerInfoResult: HttpGetResult[CustomerInformation])
    new AccountDetailsService(mockVatSubscriptionConnector)
  }

  "Calling .getAccountDetails" should {

    "retrieve the customer details" in {
      val details: HttpGetResult[CustomerInformation] = await(accountDetailsService().getAccountDetails("123456789"))
      details shouldBe customerInfoResult
    }
  }

  "Calling .getEntityName" when {

    "the connector retrieves a CustomerInformation model" should {

      "return the appropriate name" in {
        lazy val result: ServiceResponse[Option[String]] = await(accountDetailsService().getEntityName("999999999"))
        result shouldBe Right(Some("Cheapo Clothing"))
      }
    }

    "the connector returns an error" should {

      "return None" in {
        lazy val customerInfoResult: HttpGetResult[CustomerInformation] = Left(BadRequestError("", ""))
        val result: ServiceResponse[Option[String]] = await(accountDetailsService(customerInfoResult).getEntityName("999999999"))
        result shouldBe Left(CustomerInformationError)
      }
    }
  }
}
