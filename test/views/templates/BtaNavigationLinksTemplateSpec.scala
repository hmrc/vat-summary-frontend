/*
 * Copyright 2019 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html

class BtaNavigationLinksTemplateSpec extends TemplateBaseSpec {

  val btaHome = "Home"
  val btaMessages = "Messages"
  val btaManageAccount = "Manage account"
  val btaHelpAndContact = "Help and contact"

  "btaNavigationLinks" should {

    val view: Html = views.html.templates.btaNavigationLinks()(messages,mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)


    "have a link to BTA home" which {

      lazy val homeLink = document.getElementById("service-info-home-link")

      "should have the text home" in {
        homeLink.text() shouldBe btaHome
      }

      "should have a link to home" in {
        homeLink.attr("href") shouldBe mockAppConfig.btaHomeUrl
      }

    }

    "have a link to BTA Manage Account" which {

      lazy val manageAccountLink = document.getElementById("service-info-manage-account-link")

      "should have the text Manage account" in {
        manageAccountLink.text() shouldBe btaManageAccount
      }

      "should have a link to Manage account" in {
        manageAccountLink.attr("href") shouldBe mockAppConfig.btaManageAccountUrl
      }

    }

    "have a link to BTA Messages" which {

      lazy val messagesLink = document.getElementById("service-info-messages-link")

      "should have the text Messages" in {
        messagesLink.text() shouldBe btaMessages
      }

      "should have a link to Messages" in {
        messagesLink.attr("href") shouldBe mockAppConfig.btaMessagesUrl
      }

    }

    "have a link to BTA Help and contact" which {

      lazy val helpAndContactLink = document.getElementById("service-info-help-and-contact-link")

      "should have the text Help and contact" in {
        helpAndContactLink.text() shouldBe btaHelpAndContact
      }

      "should have a link to Help and contact" in {
        helpAndContactLink.attr("href") shouldBe mockAppConfig.btaHelpAndContactUrl
      }

    }

  }
}


