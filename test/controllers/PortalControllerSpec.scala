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

import audit.AuditingService
import audit.models.ExtendedAuditModel
import common.TestModels.successfulAuthResult
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PortalControllerSpec extends ControllerBaseSpec {

  private trait PortalControllerTest {
    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult

    val mockAuditService: AuditingService = mock[AuditingService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})
    }

    def target: PortalController = {
      setup()
      new PortalController(
        authorisedController,
        mockAuditService,
        mockAppConfig,
        mcc,
        ec
      )
    }
  }

  "Calling the HybridWYO method" when {

    "the user is authorised" should {

      "redirect to the portal" in new PortalControllerTest {

        lazy val result: Future[Result] = target.hybridWYO()(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("/whatYouOwePortal")

      }

    }

  }

  "Calling the HybridPH method" when {

    "the user is authorised" should {

      "redirect to the portal" in new PortalControllerTest {

        lazy val result: Future[Result] = target.hybridPH()(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("/paymentHistoryPortal")

      }

    }

  }

}
