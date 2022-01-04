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

package connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.{NavContent, NavLinks}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.ServiceInfoStub
import uk.gov.hmrc.http.HeaderCarrier

class ServiceInfoPartialConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val connector: ServiceInfoPartialConnector = app.injector.instanceOf[ServiceInfoPartialConnector]
  }

  "ServiceInfoPartialConnector" should {

    "return a NavContent model when receiving appropriate JSON from the API" in new Test {
      override def setupStubs(): StubMapping = ServiceInfoStub.stubServiceInfoPartial
      val navContent: NavContent = NavContent(
        NavLinks("Home", "Hafan", "http://localhost:9999/home"),
        NavLinks("Account", "Crfrif", "http://localhost:9999/account"),
        NavLinks("Messages", "Negeseuon", "http://localhost:9999/messages", Some(1)),
        NavLinks("Help", "Cymorth", "http://localhost:9999/help")
      )
      setupStubs()
      val result: Option[NavContent] = await(connector.getNavLinks())

      result shouldBe Some(navContent)
    }

    "return None when invalid JSON has been received from the API" in new Test {
      override def setupStubs(): StubMapping = ServiceInfoStub.stubInvalidJson
      setupStubs()
      val result: Option[NavContent] = await(connector.getNavLinks())

      result shouldBe None
    }
  }
}
