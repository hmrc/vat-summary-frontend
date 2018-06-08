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

package controllers

import audit.AuditingService
import audit.models.AuditModel
import connectors.DDConnector
import play.api.http.Status
import play.api.mvc.Result
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class DDControllerSpec extends ControllerBaseSpec {

  private val vrn = "123456789"

  private trait ConnectToDirectDebitTest {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", vrn)), "")
      )))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockDDConnector: DDConnector = mock[DDConnector]
    val mockAuditService: AuditingService = mock[AuditingService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})

    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: DDController = {
      setup()
      new DDController(
        messages,
        mockEnrolmentsAuthService,
        mockDDConnector,
        mockAppConfig,
        mockAuditService
      )

    }
  }

  "Calling the directDebits action" when {

    "the user is logged in" should {
      "redirected " in new ConnectToDirectDebitTest {

        val redirectUrl = "www.google.co.uk"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()
          (mockDDConnector.startJourney(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(vrn, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.directDebit()(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

    }

  }


}
