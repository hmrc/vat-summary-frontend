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

package services

import common.TestModels.navContent
import connectors.ServiceInfoPartialConnector
import controllers.ControllerBaseSpec
import models.{ListLinks, User}
import play.api.i18n.{Lang, Messages, MessagesImpl}
import play.twirl.api.{Html, HtmlFormat}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.templates.NavLinksView

import scala.concurrent.{ExecutionContext, Future}

class ServiceInfoServiceSpec extends ControllerBaseSpec {

  val mockConnector: ServiceInfoPartialConnector = mock[ServiceInfoPartialConnector]
  val btaLinks: NavLinksView = injector.instanceOf[NavLinksView]
  val service: ServiceInfoService = new ServiceInfoService(mockConnector, btaLinks)
  val hc: HeaderCarrier = HeaderCarrier()
  val user: User = User("123123123")
  val agentUser: User = User("123123123", arn = Some("XARN1234567"))

  ".getPartial" should {

    "return the BTA nav HTML for principal users" in {
      (mockConnector.getNavLinks()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
        .returning(Future.successful(Some(navContent)))
      val listLinks = Seq(
        ListLinks(navContent.home.en, navContent.home.url),
        ListLinks(navContent.account.en, navContent.account.url),
        ListLinks(navContent.messages.en, navContent.messages.url, navContent.messages.alerts.map(_.toString)),
        ListLinks(navContent.help.en, navContent.help.url)
      )
      val result: Html = await(service.getPartial(user, hc, ec, messages))
      val expectedResult: Html = btaLinks(listLinks)

      result.body shouldBe expectedResult.body
    }

    "return empty HTML for agents" in {
      val result: Html = await(service.getPartial(agentUser, hc, ec, messages))
      val expectedResult: Html = HtmlFormat.empty

      result shouldBe expectedResult
    }
  }

  ".notificationBadgeCount" should {

    "return the specified number as a string" in {
      service.notificationBadgeCount(0) shouldBe "0"
      service.notificationBadgeCount(1) shouldBe "1"
      service.notificationBadgeCount(99) shouldBe "99"
    }

    "return '+99' when the number is greater than 99" in {
      service.notificationBadgeCount(100) shouldBe "+99"
    }
  }

  ".partialList" when {

    "provided with some nav content and a language of 'en'" should {

      "return a sequence of English list links" in {
        implicit val messages: Messages = MessagesImpl(Lang("en"), messagesApi)
        val expectedListLinks = Seq(
          ListLinks("Home", "http://localhost:9999/home"),
          ListLinks("Account", "http://localhost:9999/account"),
          ListLinks("Messages", "http://localhost:9999/messages", Some("1")),
          ListLinks("Help", "http://localhost:9999/help")
        )

        service.partialList(Some(navContent)) shouldBe expectedListLinks
      }
    }

    "provided with some nav content and a language of 'cy'" should {

      "return a sequence of Welsh list links" in {
        implicit val messages: Messages = MessagesImpl(Lang("cy"), messagesApi)
        val expectedListLinks = Seq(
          ListLinks("Hafan", "http://localhost:9999/home"),
          ListLinks("Crfrif", "http://localhost:9999/account"),
          ListLinks("Negeseuon", "http://localhost:9999/messages", Some("1")),
          ListLinks("Cymorth", "http://localhost:9999/help")
        )

        service.partialList(Some(navContent)) shouldBe expectedListLinks
      }
    }

    "provided with no nav content" should {

      "return an empty sequence" in {
        service.partialList(None) shouldBe Seq()
      }
    }
  }
}
