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

package views.partials.btaHome

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ClaimEnrolmentViewSpec extends ViewBaseSpec {

  "Rendering the claim enrolment partial" should {

    object Selectors {
      val pageHeading = "h2"
      val mandationInfo = "p"
      val addEnrolmentLink = "a"
    }

    val testVrn: String = "123456789"

    lazy val view = views.html.partials.btaHome.claimEnrolment(testVrn)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Your agent has signed you up to Making Tax Digital for VAT"
    }

    "have the correct mandation information message on the page" in {
      elementText(Selectors.mandationInfo) shouldBe
        "From April 2019, VAT registered businesses with a turnover of £85,000 or above must use relevant third party software to submit their VAT Returns."
    }

    "have the correct link address" in {
      element(Selectors.addEnrolmentLink).attr("href") shouldBe "mtd-claim-subscription/123456789"
    }
  }
}
