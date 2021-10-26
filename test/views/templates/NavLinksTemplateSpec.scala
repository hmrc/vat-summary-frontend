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

package views.templates

import models.ListLinks
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.templates.NavLinksView

class NavLinksTemplateSpec extends ViewBaseSpec {

  val navLinksView: NavLinksView = injector.instanceOf[NavLinksView]

  val btaHome = "Home"
  val btaMessages = "Messages 3"
  val btaManageAccount = "Manage account"
  val btaHelpAndContact = "Help and contact"

  val navLinksHome: ListLinks = ListLinks("Home", "/business-account", Some("0"), Some(true))
  val navLinksAccount: ListLinks = ListLinks("Manage account", "/manage-account", Some("0"), Some(true))
  val navLinksMessage: ListLinks = ListLinks("Messages", "/messages", Some("3"), Some(true))
  val navLinksHelpAndContact: ListLinks = ListLinks("Help and contact", "/help", Some("0"), Some(true))
  val navLinksHideMenu: ListLinks = ListLinks("Home", "/business-account", Some("0"), Some(false))


  "navLinks" should {

    "have a link to BTA home" which {

      val view: Html = navLinksView(Seq(navLinksHome))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      lazy val homeLink = document.getElementById("nav-bar-link-Home")

      "should have the text home" in {
        homeLink.text() shouldBe btaHome
      }

      "should have a link to home" in {
        homeLink.attr("href") shouldBe "/business-account"
      }
    }

    "have a link to BTA Manage Account" which {

      val view: Html = navLinksView(Seq(navLinksAccount))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      lazy val manageAccountLink = document.getElementById("nav-bar-link-Manage account")

      "should have the text Manage account" in {
        manageAccountLink.text() shouldBe btaManageAccount
      }

      "should have a link to Manage account" in {
        manageAccountLink.attr("href") shouldBe "/manage-account"
      }
    }

    "have a link to BTA Messages" which {

      val view: Html = navLinksView(Seq(navLinksMessage))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      lazy val messagesLink = document.getElementById("nav-bar-link-Messages")

      "should have the text Messagees and show the number of alerts" in {
        messagesLink.text() shouldBe btaMessages
      }

      "should have a link to Messages" in {
        messagesLink.attr("href") shouldBe "/messages"
      }

      "should have 3 alerts" in {
        elementText(".hmrc-notification-badge") shouldBe "3"

      }
    }

    "have a link to BTA Help and contact" which {

      val view: Html = navLinksView(Seq(navLinksHelpAndContact))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      lazy val helpAndContactLink = document.getElementById("nav-bar-link-Help and contact")

      "should have the text Help and contact" in {
        helpAndContactLink.text() shouldBe btaHelpAndContact
      }

      "should have a link to Help and contact" in {
        helpAndContactLink.attr("href") shouldBe "/help"
      }
    }

    "display multiple links" which {

      val view: Html = navLinksView(Seq(navLinksHome, navLinksMessage))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      "should have the correct link names" in {
        elementText(".hmrc-account-menu__main > li:nth-child(1) > a") shouldBe btaHome
        elementText(".hmrc-account-menu__main > li:nth-child(2) > a") shouldBe btaMessages
      }

      "should have the correct link URL's" in {
        element(".hmrc-account-menu__main > li:nth-child(1) > a").attr("href") shouldBe "/business-account"
        element(".hmrc-account-menu__main > li:nth-child(2) > a").attr("href") shouldBe "/messages"
      }
    }

    "not display when required" which {

      val view: Html = navLinksView(Seq(navLinksHideMenu))(messages)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      "should not display the link" in {
        elementExtinct(".hmrc-account-menu__main > li:nth-child(1) > a")
      }
    }

  }
}


