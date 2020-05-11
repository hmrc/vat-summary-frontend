/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.partials

import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import views.html.partials.btaHome.{ClaimEnrolment, PartialMigration, VatSection}

import scala.concurrent.{ExecutionContext, Future}

class BtaHomeControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val authResult: Future[Enrolments]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    def setup(): Any ={
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val claimEnrolmentView: ClaimEnrolment = injector.instanceOf[ClaimEnrolment]
    val vatSectionView: VatSection = injector.instanceOf[VatSection]
    val partialMigrationView: PartialMigration = injector.instanceOf[PartialMigration]

    def target: BtaHomeController = {
      setup()
      new BtaHomeController(
        mockEnrolmentsAuthService,
        mockAppConfig,
        mcc,
        ec,
        claimEnrolmentView,
        vatSectionView,
        partialMigrationView)
    }
  }

  "Calling the .vatSection action" when {

    "A user is logged in and enrolled to HMRC-MTD-VAT" should {

      val goodEnrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "HMRC-MTD-VAT",
            Seq(EnrolmentIdentifier("VRN", "123456789")),
            "Active")
        )
      )

      "return 200" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.vatSection()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.vatSection()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset of utf-8" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.vatSection()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "A user is logged in but not enrolled to HMRC-MTD-VAT" should {

      "return FORBIDDEN" in new Test {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        private val result = target.vatSection()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "A user is not logged in" should {

      "return UNAUTHORIZED" in new Test {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        private val result = target.vatSection()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "Auth call was successful but VRN not found in enrolments" should {

      val enrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "",
            Seq(EnrolmentIdentifier("NOT-VRN", "abc")),
            "Active")
        )
      )

      "return INTERNAL_SERVER_ERROR" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(enrolments)
        private val result = target.vatSection()(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling the .claimEnrolment action" when {

    "A user is logged in and enrolled to HMCE-VATDEC-ORG" should {

      val goodEnrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "HMCE-VATDEC-ORG",
            Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
            "Active")
        )
      )

      "return 200" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.claimEnrolment()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.claimEnrolment()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset of utf-8" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.claimEnrolment()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "A signed in user attempts see the claim enrolment partial but doesn't have an HMCE-VATDEC-ORG enrolment" should {

      "return FORBIDDEN" in new Test {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        private val result = target.claimEnrolment()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "A user is not logged in" should {

      "return UNAUTHORIZED" in new Test {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        private val result = target.claimEnrolment()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "Auth call was successful but VRN not found in enrolments" should {

      val enrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "",
            Seq(EnrolmentIdentifier("NOT-VRN", "abc")),
            "Active")
        )
      )

      "return INTERNAL_SERVER_ERROR" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(enrolments)
        private val result = target.claimEnrolment()(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling the .partialMigration action" when {

    "A user is logged in and enrolled to HMCE-VATDEC-ORG" should {

      val goodEnrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "HMCE-VATDEC-ORG",
            Seq(EnrolmentIdentifier("VATRegNo", "123456789")),
            "Active")
        )
      )

      "return 200" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.partialMigration()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.partialMigration()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset of utf-8" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(goodEnrolments)
        private val result = target.partialMigration()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "A signed in user attempts see the partial migration partial but doesn't have an HMCE-VATDEC-ORG enrolment" should {

      "return FORBIDDEN" in new Test {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        private val result = target.partialMigration()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "A user is not logged in" should {

      "return UNAUTHORIZED" in new Test {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        private val result = target.partialMigration()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "Auth call was successful but VRN not found in enrolments" should {

      val enrolments: Enrolments = Enrolments(
        Set(
          Enrolment(
            "",
            Seq(EnrolmentIdentifier("NOT-VRN", "abc")),
            "Active")
        )
      )

      "return INTERNAL_SERVER_ERROR" in new Test {
        override val authResult: Future[Enrolments] = Future.successful(enrolments)
        private val result = target.partialMigration()(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
