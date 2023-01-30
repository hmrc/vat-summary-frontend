/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import config.AppConfig
import models.ServiceResponse
import models.errors.TimeToPayRedirectError
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._
import services.TimeToPayService

import scala.concurrent.{ExecutionContext, Future}

class TimeToPayControllerSpec extends ControllerBaseSpec {

  val mockTTPService: TimeToPayService = mock[TimeToPayService]

  def mockTTPServiceCall(serviceResponse: ServiceResponse[String]): Any =
    (mockTTPService.retrieveRedirectUrl(_: ExecutionContext, _: AppConfig))
      .expects(*,*).returns(Future.successful(serviceResponse))

  val controller = new TimeToPayController(authorisedController, mcc, mockTTPService, mockServiceErrorHandler)

  "The redirect action" when {

    "the TTP feature switch is on" when {

      "the service returns a URL" should {

        lazy val result = {
          mockAppConfig.features.overdueTimeToPayDescriptionEnabled(true)
          mockPrincipalAuth()
          mockTTPServiceCall(Right("/example-url"))
          controller.redirect(fakeRequest)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect the request to the provided URL" in {
          redirectLocation(result) shouldBe Some("/example-url")
        }
      }

      "the service returns an error" should {

        lazy val result = {
          mockAppConfig.features.overdueTimeToPayDescriptionEnabled(true)
          mockPrincipalAuth()
          mockTTPServiceCall(Left(TimeToPayRedirectError))
          controller.redirect(fakeRequest)
        }

        "return 500" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "render the technical difficulties page" in {
          Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - VAT - GOV.UK"
        }
      }
    }

    "the TTP feature switch is off" should {

      lazy val result = {
        mockAppConfig.features.overdueTimeToPayDescriptionEnabled(false)
        mockPrincipalAuth()
        controller.redirect(fakeRequest)
      }

      "return 404" in {
        status(result) shouldBe Status.NOT_FOUND
      }

      "render the not found page" in {
        Jsoup.parse(contentAsString(result)).title shouldBe "Page not found - VAT - GOV.UK"
      }
    }
  }
}
