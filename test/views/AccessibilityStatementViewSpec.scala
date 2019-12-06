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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class AccessibilityStatementViewSpec extends ViewBaseSpec {

  "The accessibility statement page" when {

    lazy val fakeUri: String = "/vat-through-software/vat-overview"

    "configured to be partially compliant" should {

      lazy val view = views.html.accessibility_statement(fakeUri, fullyCompliant = false)
      implicit lazy val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Accessibility statement for Making Tax Digital for VAT - Business tax account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "Accessibility statement for Making Tax Digital for VAT"
      }

      "have an introduction section" which {

        "has the correct lede paragraph" in {
          elementText(".lede") shouldBe "This accessibility statement explains how accessible this service is, " +
            "what to do if you have difficulty using it, and how to report accessibility problems with the service."
        }

        "has a second paragraph" which {

          "has the correct content" in {
            elementText("#introduction > p:nth-of-type(2)") shouldBe "This service is part of the wider " +
              "GOV.UK website. There is a separate accessibility statement for the main GOV.UK website."
          }

          "has the correct link text" in {
            elementText("#introduction > p:nth-of-type(2) > a") shouldBe "accessibility statement"
          }

          "has the correct link location" in {
            element("#introduction > p:nth-of-type(2) > a").attr("href") shouldBe mockConfig.govUkAccessibilityUrl
          }
        }

        "has a third paragraph" which {

          "has the correct content" in {
            elementText("#introduction > p:nth-of-type(3)") shouldBe "This page only contains information " +
              "about the Making Tax Digital for VAT service, available at " +
              "https://www.tax.service.gov.uk/vat-through-software/vat-overview."
          }

          "has the correct link text" in {
            elementText("#introduction > p:nth-of-type(3) > a") shouldBe
              "https://www.tax.service.gov.uk/vat-through-software/vat-overview"
          }

          "has the correct link location" in {
            element("#introduction > p:nth-of-type(3) > a").attr("href") shouldBe mockConfig.govUkHMRCUrl
          }
        }
      }

      "have a section on using this service" which {

        "has the correct heading" in {
          elementText("#using-this-service > h2") shouldBe "Using this service"
        }

        "has the correct first paragraph" in {
          elementText("#using-this-service > p:nth-of-type(1)") shouldBe "VAT registered businesses, and their " +
            "agents, can sign up to use compatible software to submit VAT Returns to HMRC as part of the Making " +
            "Tax Digital for VAT initiative. A Business tax account is created for businesses and their agents where " +
            "they can:"
        }

        "has a bullet list of MTD features with the correct elements" in {
          elementText("#using-this-service > ul:nth-of-type(1)") shouldBe
            "change their business name if they’re a limited company " +
              "update their principal place of business " +
              "view their VAT certificate " +
              "change their VAT Return dates " +
              "cancel their VAT registration " +
              "opt out of Making Tax Digital"
        }

        "has the correct second paragraph" in {
          elementText("#using-this-service > p:nth-of-type(2)") shouldBe "This service is run by HM Revenue and " +
            "Customs (HMRC). We want as many people as possible to be able to use this service. This means you should " +
            "be able to:"
        }

        "has a bullet list of accessibility features" in {
          elementText("#using-this-service > ul:nth-of-type(2)") shouldBe
            "change colours, contrast levels and fonts " +
              "zoom in up to 300% without the text spilling off the screen " +
              "get from the start of the service to the end using just a keyboard " +
              "get from the start of the service to the end using speech recognition software " +
              "listen to the service using a screen reader (including the most recent versions of JAWS, NVDA and VoiceOver)"
        }

        "has the correct third paragraph" in {
          elementText("#using-this-service > p:nth-of-type(3)") shouldBe
            "We have also made the text in this service as simple as possible to understand."
        }

        "has a fourth paragraph" which {

          "has the correct content" in {
            elementText("#using-this-service > p:nth-of-type(4)") shouldBe
              "AbilityNet has advice on making your device easier to use if you have a disability."
          }

          "has the correct link text" in {
            elementText("#using-this-service > p:nth-of-type(4) > a") shouldBe "AbilityNet"
          }

          "has the correct link location" in {
            element("#using-this-service > p:nth-of-type(4) > a").attr("href") shouldBe mockConfig.abilityNetUrl
          }
        }
      }

      "have a section on how accessible this service is" which {

        "has the correct heading" in {
          elementText("#how-accessible > h2") shouldBe "How accessible this service is"
        }

        "has a first paragraph" which {

          "has the correct content" in {
            elementText("#how-accessible > p:nth-of-type(1)") shouldBe
              "This service is partially compliant with the Web Content Accessibility Guidelines version 2.1 AA standard."
          }

          "has the correct link text" in {
            elementText("#how-accessible > p:nth-of-type(1) > a") shouldBe
              "Web Content Accessibility Guidelines version 2.1 AA standard"
          }

          "has the correct link location" in {
            element("#how-accessible > p:nth-of-type(1) > a").attr("href") shouldBe mockConfig.wcagGuidelinesUrl
          }
        }

        "has the correct second paragraph" in {
          elementText("#how-accessible > p:nth-of-type(2)") shouldBe
            "Some people may find parts of this service difficult to use:"
        }

        "has a bullet list of service difficulties" in {
          elementText("#how-accessible > ul") shouldBe
            "On the ‘Payment History’ and ‘Past Returns’ pages, when the tabs are clicked, the user is not told that " +
              "the page had been refreshed " +
              "On the ‘Payment History’ and ‘Past Returns’ pages, when the user clicks on a tab, the page focus is sent " +
              "to the top of the page"
        }
      }

      "have a section on reporting accessibility problems" which {

        "has the correct heading" in {
          elementText("#reporting-problems > h2") shouldBe "Reporting accessibility problems with this service"
        }

        "has a paragraph" which {

          "has the correct content" in {
            elementText("#reporting-problems > p") shouldBe "We are always looking to improve the accessibility of " +
              "this service. If you find any problems that are not listed on this page or think we are not meeting " +
              "accessibility requirements, report the accessibility problem."
          }

          "has the correct link text" in {
            elementText("#reporting-problems > p > a") shouldBe "report the accessibility problem"
          }

          "has the correct link location" in {
            element("#reporting-problems > p > a")
              .attr("href") shouldBe mockConfig.reportA11yProblemUrl + s"&userAction=$fakeUri"
          }
        }
      }

      "have a section on responding to complaints" which {

        "has the correct heading" in {
          elementText("#complaints > h2") shouldBe "What to do if you are not happy with how we respond to your complaint"
        }

        "has a paragraph" which {

          "has the correct content" in {
            elementText("#complaints > p") shouldBe "The Equality and Human Rights Commission (EHRC) is responsible " +
              "for enforcing the Public Sector Bodies (Websites and Mobile Applications) (No.2) Accessibility " +
              "Regulations 2018 (the ‘accessibility regulations’). If you are happy with how we respond to your " +
              "complaint, contact the Equality Advisory and Support Service (EASS), or the Equality Commission for " +
              "Northern Ireland (ECNI) if you live in Northern Ireland."
          }

          "has the correct link text for the first link" in {
            elementText("#complaints > p > a:nth-of-type(1)") shouldBe "contact the Equality Advisory and Support Service"
          }

          "has the correct link location for the first link" in {
            element("#complaints > p > a:nth-of-type(1)").attr("href") shouldBe mockConfig.eassUrl
          }

          "has the correct link text for the second link" in {
            elementText("#complaints > p > a:nth-of-type(2)") shouldBe "Equality Commission for Northern Ireland"
          }

          "has the correct link location for the second link" in {
            element("#complaints > p > a:nth-of-type(2)").attr("href") shouldBe mockConfig.ecniUrl
          }
        }
      }

      "have a section on contacting HMRC" which {

        "has the correct heading" in {
          elementText("#contact-us > h2") shouldBe "Contacting us by phone or getting a visit from us in person"
        }

        "has the correct first paragraph" in {
          elementText("#contact-us > p:nth-of-type(1)") shouldBe
            "We provide a text relay service if you are deaf, hearing impaired or have a speech impediment."
        }

        "has the correct second paragraph" in {
          elementText("#contact-us > p:nth-of-type(2)") shouldBe "We can provide a British Sign Language (BSL) " +
            "interpreter, or you can arrange a visit from a HMRC advisor to help you complete the service."
        }

        "has a third paragraph" which {

          "has the correct content" in {
            elementText("#contact-us > p:nth-of-type(3)") shouldBe "Find out how to contact us."
          }

          "has the correct link text" in {
            elementText("#contact-us > p:nth-of-type(3) > a") shouldBe "contact us"
          }

          "has the correct link location" in {
            element("#contact-us > p:nth-of-type(3) > a").attr("href") shouldBe mockConfig.govUkHearingImpairedUrl
          }
        }
      }

      "have a section on technical information" which {

        "has the correct heading" in {
          elementText("#technical-information > h2") shouldBe "Technical information about this service’s accessibility"
        }

        "has the correct first paragraph" in {
          elementText("#technical-information > p:nth-of-type(1)") shouldBe "HMRC is committed to making this service " +
            "accessible, in accordance with the Public Sector Bodies (Websites and Mobile Applications) (No. 2) " +
            "Accessibility Regulations 2018."
        }

        "has the correct second paragraph" which {

          "has the correct content" in {
            elementText("#technical-information > p:nth-of-type(2)") shouldBe "This service is partially compliant " +
              "with the Web Content Accessibility Guidelines version 2.1 AA standard, due to the non-compliances " +
              "listed below."
          }

          "has the correct link text" in {
            elementText("#technical-information > p:nth-of-type(2) > a") shouldBe
              "Web Content Accessibility Guidelines version 2.1 AA standard"
          }

          "has the correct link location" in {
            element("#technical-information > p:nth-of-type(2) > a").attr("href") shouldBe mockConfig.wcagGuidelinesUrl
          }
        }
      }

      "have a section on non accessible content" which {

        "has the correct heading" in {
          elementText("#non-accessible > h3") shouldBe "Non accessible content"
        }

        "has the correct first paragraph" in {
          elementText("#non-accessible > p") shouldBe
            "The content listed below is non-accessible for the following reasons."
        }
      }

      "have a section on non compliance" which {

        "has the correct heading" in {
          elementText("#non-compliance > h3") shouldBe "Non-compliance with the accessibility regulations"
        }

        "has the correct first paragraph" in {
          elementText("#non-compliance > p") shouldBe
            "All of these accessibility problems will be fixed at the end of January 2020."
        }

        "has a bullet list of service difficulties" in {
          elementText("#non-compliance > ul") shouldBe
            "On the ‘Payment History’ and ‘Past Returns’ pages, when the tabs are clicked, the user is not told that " +
              "the page had been refreshed This doesn’t meet WCAG 2.1 success criterion 4.1.2 Name, Role, Value (A) " +
              "On the ‘Payment History’ and ‘Past Returns’ pages, when the user clicks on a tab, the page focus is sent " +
              "to the top of the page This doesn’t meet WCAG 2.1 success criterion 2.4.3 Focus Order (A)"
        }
      }

      "have a section on how we tested this service" which {

        "has the correct heading" in {
          elementText("#how-we-tested > h2") shouldBe "How we tested this service"
        }

        "has the correct first paragraph" in {
          elementText("#how-we-tested > p:nth-of-type(1)") shouldBe
            "The service was last tested on 3 September 2019 and was checked for compliance with WCAG 2.1 AA."
        }

        "has a second paragraph" which {

          "has the correct content" in {
            elementText("#how-we-tested > p:nth-of-type(2)") shouldBe "The service was built using parts that were " +
              "tested by the Digital Accessibility Centre. The full service was tested by HMRC and included disabled users."
          }

          "has the correct link text" in {
            elementText("#how-we-tested > p:nth-of-type(2) > a") shouldBe "Digital Accessibility Centre"
          }

          "has the correct link location" in {
            element("#how-we-tested > p:nth-of-type(2) > a").attr("href") shouldBe mockConfig.dacUrl
          }
        }

        "has the correct third paragraph" in {
          elementText("#how-we-tested > p:nth-of-type(3)") shouldBe
            "This page was prepared on 20 September 2019. It was last updated on 20 September 2019."
        }
      }
    }

    "configured to be fully compliant" should {

      lazy val view = views.html.accessibility_statement(fakeUri, fullyCompliant = true)
      implicit lazy val document: Document = Jsoup.parse(view.body)

      "have a section on how accessible this service is" which {

        "has a first paragraph" which {

          "has the correct content" in {
            elementText("#how-accessible > p:nth-of-type(1)") shouldBe
              "This service is fully compliant with the Web Content Accessibility Guidelines version 2.1 AA standard."
          }

          "has the correct link text" in {
            elementText("#how-accessible > p:nth-of-type(1) > a") shouldBe
              "Web Content Accessibility Guidelines version 2.1 AA standard"
          }

          "has the correct link location" in {
            element("#how-accessible > p:nth-of-type(1) > a").attr("href") shouldBe mockConfig.wcagGuidelinesUrl
          }
        }

        "has a second paragraph" in {
          elementText("#how-accessible > p:nth-of-type(2)") shouldBe
            "There are no known accessibility issues within this service."
        }

        "does not have a bullet list of service difficulties" in {
          elementExtinct("#how-accessible > ul")
        }
      }

      "have a section on technical information" which {

        "has the correct second paragraph" which {

          "has the correct content" in {
            elementText("#technical-information > p:nth-of-type(2)") shouldBe "This service is fully compliant " +
              "with the Web Content Accessibility Guidelines version 2.1 AA standard."
          }

          "has the correct link text" in {
            elementText("#technical-information > p:nth-of-type(2) > a") shouldBe
              "Web Content Accessibility Guidelines version 2.1 AA standard"
          }

          "has the correct link location" in {
            element("#technical-information > p:nth-of-type(2) > a").attr("href") shouldBe mockConfig.wcagGuidelinesUrl
          }
        }
      }

      "not have a section on non accessible content" in {
        elementExtinct("#non-accessible")
      }

      "not have a section on non compliance" in {
        elementExtinct("#non-compliance")
      }
    }
  }
}
