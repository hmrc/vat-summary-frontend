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

package connectors

import common.TestModels.navContent
import controllers.ControllerBaseSpec
import models.NavContent
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.http.HttpClient
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.{ExecutionContext, Future, TimeoutException}

class ServiceInfoPartialConnectorSpec extends ControllerBaseSpec {

  val httpClient: HttpClient = mock[HttpClient]
  val connector = new ServiceInfoPartialConnector(httpClient)(mockAppConfig)
  implicit val hc: HeaderCarrier = HeaderCarrier()
  def mockNavLinksCall(result: Future[Option[NavContent]]): Any =
    (httpClient.GET[Option[NavContent]](_: String, _: Seq[(String, String)], _: Seq[(String, String)])
                                       (_: HttpReads[Option[NavContent]],_: HeaderCarrier,_: ExecutionContext))
      .stubs(*,*,*,*,*,*)
      .returns(result)

  "ServiceInfoPartialConnector" should {

    "generate the correct url" in {
      connector.btaUrl shouldBe "/business-account/partial/nav-links"
    }
  }

  ".getNavLinks" when {

    "a successful response is returned" should {

      "return the NavLinks model" in {
        mockNavLinksCall(Future.successful(Some(navContent)))
        await(connector.getNavLinks()) shouldBe Some(navContent)
      }
    }

    "an exception is thrown when attempting to retrieve the BTA nav links" should {

      "return None" in {
        mockNavLinksCall(Future.failed(new TimeoutException("FAILURE")))
        await(connector.getNavLinks()) shouldBe None
      }
    }
  }
}