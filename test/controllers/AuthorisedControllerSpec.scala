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

package controllers

import common.SessionKeys
import common.TestModels.{customerInformationInsolvent, customerInformationMax}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.{AnyContent, Request, Result}
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import models.errors.UnknownError
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments, InsufficientEnrolments}


import scala.concurrent.{ExecutionContext, Future}

class AuthorisedControllerSpec extends ControllerBaseSpec {

  def target(request: Request[AnyContent]): Future[Result] = authorisedController.authorisedAction({
    _ =>
      _ =>
        Future.successful(Ok("welcome"))
  })(request)

  def mockAuth(authResponse: Future[~[Enrolments, Option[AffinityGroup]]]): Any = {
  (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
    .expects(*, *, *, *)
    .returns(authResponse)
  }
  val enrolments: Set[Enrolment] = Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), ""))

  val successfulAuthResponse: Future[~[Enrolments, Option[AffinityGroup]]] = Future.successful(new ~(
    Enrolments(enrolments),
    Some(Individual)
  ))

  "The AuthPredicate when the user is an Individual (Principle Entity)" when {

    "they have an active HMRC-MTD-VAT enrolment" when {

      "they have a value in session for their insolvency status" when {

        "the value is 'true' (insolvent user not continuing to trade)" should {

          lazy val result = {
            mockAuth(successfulAuthResponse)
            target(insolventRequest)
          }
          "return Forbidden (403)" in {
            status(result) shouldBe Status.FORBIDDEN
          }
        }

        "the value is 'false' (user permitted to trade)" should {

          lazy val result = {
            mockAuth(successfulAuthResponse)
            target(fakeRequest)
          }

          "return OK (200)" in {
            status(result) shouldBe Status.OK
          }
        }
      }

      "they do not have a value in session for their insolvency status" when {

        "they are insolvent and not continuing to trade" should {

          lazy val result = {
            mockAuth(successfulAuthResponse)
            mockCustomerInfo(Future.successful(Right(customerInformationInsolvent)))
            target(FakeRequest())
          }

          "return Forbidden (403)" in {
            status(result) shouldBe Status.FORBIDDEN
          }

          "add the insolvent flag to the session" in {
            session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("true")
          }
        }

        "they are permitted to trade" should {

          lazy val result = {
            mockAuth(successfulAuthResponse)
            mockCustomerInfo(Future.successful(Right(customerInformationMax)))
            target(FakeRequest())
          }

          "return OK (200)" in {
            status(result) shouldBe Status.OK
          }

          "add the insolvent flag to the session" in {
            session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("false")
          }
        }

        "there is an error returned from the customer information API" should {

          lazy val result = {
            mockAuth(successfulAuthResponse)
            mockCustomerInfo(Future.successful(Left(UnknownError)))
            target(FakeRequest())
          }

          "return Internal Server Error (500)" in {
            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }
    }

    "they do NOT have an active HMRC-MTD-VAT enrolment" should {

      lazy val result :Future[Result] = {
        mockAuth(Future.failed(InsufficientEnrolments()))
        target(fakeRequest)
      }
      "return Forbidden (403)" in {
        status(result) shouldBe Status.FORBIDDEN
      }

      "render the Not Signed Up page" in {
        Jsoup.parse(contentAsString(result)).title shouldBe "You are not authorised to use this service - VAT - GOV.UK"
      }
    }
  }
}


