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

import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.{Address, CustomerInformation}
import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AccountDetailsServiceSpec extends UnitSpec with MockFactory with Matchers {

  private trait Test {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
    lazy val service = new AccountDetailsService(mockVatSubscriptionConnector)
  }

  val responseFromConnector = Right(CustomerInformation(
    Some("Cheapo Clothing Ltd"),
    Some("Betty"),
    Some("Jones"),
    Some("Cheapo Clothing"),
    Address("Bedrock Quarry",
      "Bedrock",
      Some("Graveldon"),
      Some("Graveldon"),
      Some("GV2 4BB")
    ),
    Some("01632 982028"),
    Some("07700 900018"),
    Some("bettylucknexttime@gmail.com"),
    Address("13 Pebble Lane",
      "Bedrock",
      Some("Graveldon"),
      Some("Graveldon"),
      Some("GV13 4BJ")
    ),
    Some("01632 960026"),
    Some("07700 900018"),
    Some("bettylucknexttime@gmail.com")
  ))

  "getAccountDetails" should {
    "retrieve the customer details" in new Test {
      (mockVatSubscriptionConnector.getCustomerInfo(_: String)
      (_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .returns(Future.successful(responseFromConnector))

      val details: HttpGetResult[CustomerInformation] = await(service.getAccountDetails("123456789"))
      details shouldBe responseFromConnector
    }

  }
}
