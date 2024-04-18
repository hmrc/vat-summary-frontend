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

package views.templates

import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec
import views.html.templates.HowInterestIsCalculated

class HowInterestIsCalculatedSpec extends ViewBaseSpec {

  val viewVATLPIVersion: HtmlFormat.Appendable = app.injector.instanceOf[HowInterestIsCalculated].apply("vat")
  val viewPenaltyLPIVersion: HtmlFormat.Appendable = app.injector.instanceOf[HowInterestIsCalculated].apply("penalty")
  val viewManualLPIVersion: HtmlFormat.Appendable = app.injector.instanceOf[HowInterestIsCalculated].apply("manual")

  object Selectors {
    val dropDownLink = "#how-interest-calculated-dropdown > summary > span"
    val p1 = "#how-interest-calculated-p1"
    val p2 = "#how-interest-calculated-p2"
    val p3 = "#how-interest-calculated-p3"
    val link = "#how-interest-calculated-link"
  }

  val dropdownLinkText = "How the interest amount was calculated"
  val p1VATLPI = "The calculation we use for each day is: (Interest rate × VAT amount unpaid) ÷ days in a year"
  val p1PenaltyLPI = "The calculation we use for each day is: (Interest rate × penalty amount unpaid) ÷ days in a year"
  val p1ManualLPI = "The calculation we use for each day is: (Interest rate × amount unpaid) ÷ days in a year"
  val p2 = "The amount might be adjusted to take account of any repayment interest owed by HMRC during the same time."
  val p3: String => String = link => s"You can $link. " +
    s"If the interest rate changes during the time interest is building up, we use the old interest rate up to the change date, then the new one after that."
  val link = "find interest rates on GOV.UK (opens in a new tab)"

  val expectedContentVAT: Seq[(String, String)] = Seq(
    Selectors.dropDownLink -> dropdownLinkText,
    Selectors.p1 -> p1VATLPI,
    Selectors.p2 -> p2,
    Selectors.p3 -> p3(link)
  )

  val expectedContentPenalty: Seq[(String, String)] = Seq(
    Selectors.dropDownLink -> dropdownLinkText,
    Selectors.p1 -> p1PenaltyLPI,
    Selectors.p2 -> p2,
    Selectors.p3 -> p3(link)
  )

  val expectedContentManual: Seq[(String, String)] = Seq(
    Selectors.dropDownLink -> dropdownLinkText,
    Selectors.p1 -> p1ManualLPI,
    Selectors.p2 -> p2,
    Selectors.p3 -> p3(link)
  )

  val linkUrl: String = mockConfig.govUkPrevIntRateUrl

  "HowInterestIsCalculated" when {

    "there is a VAT LPI" must {
      implicit val document: Document = asDocument(viewVATLPIVersion)

      behave like pageWithExpectedMessages(expectedContentVAT)
      behave like pageWithLink(Selectors.link, link, linkUrl)
    }

    "there is a Penalty LPIs" must {
      implicit val document: Document = asDocument(viewPenaltyLPIVersion)

      behave like pageWithExpectedMessages(expectedContentPenalty)
      behave like pageWithLink(Selectors.link, link, linkUrl)
    }

    "there is a Manual LPI" must {
      implicit val document: Document = asDocument(viewManualLPIVersion)

      behave like pageWithExpectedMessages(expectedContentManual)
      behave like pageWithLink(Selectors.link, link, linkUrl)
    }
  }
}
