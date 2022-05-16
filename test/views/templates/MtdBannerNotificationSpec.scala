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

package views.templates

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.MtdNotificationBanner

class MtdBannerNotificationSpec extends ViewBaseSpec {

  val injectedView: MtdNotificationBanner = injector.instanceOf[MtdNotificationBanner]

  "The MTD notification banner" when {

    "the user has a mandation status of '3'" should {

      lazy val view = injectedView("Non MTDfB")
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "be displayed" in {
        element(".govuk-notification-banner")
      }

      "have the correct title" in {
        elementText(".govuk-notification-banner__title") shouldBe "Important"
      }

      "have the correct heading" in {
        elementText(".govuk-heading-m") shouldBe "The way to submit VAT returns changed on 1 April due to Making Tax Digital"
      }

      "have the correct body message" in {
        elementText(".govuk-body") shouldBe
          "You cannot use this service to submit returns for accounting periods starting after 1 April 2022. Instead, digital records must be kept and returns must be submitted using HMRC compatible software. Find out when you need to sign up and start using Making Tax Digital for VAT (opens in a new tab)."
      }

      "have a link to MTD sign up guidance page" which {

        "has the correct text" in {
          elementText("a") shouldBe "Find out when you need to sign up and start using Making Tax Digital for VAT (opens in a new tab)"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.mtdGuidance
        }
      }
    }

    "the user has a different mandation status than 3" should {

      lazy val view = injectedView("MTDfB")
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not be displayed" in {
        elementExtinct(".govuk-notification-banner")
      }
    }
  }
}
