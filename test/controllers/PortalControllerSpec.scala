/*
 * Copyright 2022 HM Revenue & Customs
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

import common.TestModels.successfulAuthResult
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~

import scala.concurrent.Future

class PortalControllerSpec extends ControllerBaseSpec {

  val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult

  def setup(): Any = {
    mockPrincipalAuth()
    mockAudit()
  }

  def portalController: PortalController = {
    setup()
    new PortalController(
      authorisedController,
      mockAuditService,
      mockAppConfig,
      mcc,
      ec
    )
  }

  "Calling the HybridWYO method" when {

    "the user is authorised" should {

      "redirect to the portal" in {

        lazy val result: Future[Result] = {
          portalController.hybridWYO()(fakeRequestWithSession)
        }

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("/whatYouOwePortal")

      }

    }

  }

  "Calling the HybridPH method" when {

    "the user is authorised" should {

      "redirect to the portal" in {

        lazy val result: Future[Result] = {
          portalController.hybridPH()(fakeRequestWithSession)
        }

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("/paymentHistoryPortal")

      }

    }

  }

}
