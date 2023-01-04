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

package controllers.predicates

import common.SessionKeys
import common.TestModels._
import controllers.ControllerBaseSpec
import models.User
import models.errors.UnknownError
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers._

import scala.concurrent.Future

class FinancialPredicateSpec extends ControllerBaseSpec {

  def target(request: Request[AnyContent]): Future[Result] = financialPredicate.authoriseFinancialAction({
    _ => _ => Future.successful(Ok("welcome"))
  })(request, User("999999999"))

  ".authoriseFinancialAction" when {

    "the user has the financialAccess value of 'true' in session" should {

      "allow the user to pass through the predicate" in {
        val result = target(fakeRequest.withSession(SessionKeys.financialAccess -> "true"))
        status(result) shouldBe Status.OK
      }
    }

    "the user has no financialAccess value in session" when {

      "they are hybrid" should {

        lazy val result = {
          mockCustomerInfo(Right(customerInformationHybrid))
          target(fakeRequest)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the VAT Overview page" in {
          redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details.url)
        }
      }

      "insolvencyDateFutureUserBlocked returns true" should {

        "return 500" in {

        val result = {
          mockDateServiceCall()
          mockCustomerInfo(Right(customerInformationInsolventFuture))
          target(fakeRequest)
        }
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "they are not hybrid or blocked for insolvent reasons" should {

        lazy val result = {
          mockDateServiceCall()
          mockCustomerInfo(Right(customerInformationMax))
          target(fakeRequest)
        }

        "allow the user to pass through the predicate" in {
          status(result) shouldBe Status.OK
        }

        "add two values to session to fast-track future requests" in {
          session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("false")
          session(result).get(SessionKeys.financialAccess) shouldBe Some("true")
        }
      }

      "the customer info call fails" should {

        "return 500" in {
          lazy val result = {
            mockCustomerInfo(Left(UnknownError))
            target(fakeRequest)
          }
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
