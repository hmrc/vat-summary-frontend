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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.AccessibilityStatement

class AccessibilityStatementViewSpec extends ViewBaseSpec {

  val accessibilityStatementView: AccessibilityStatement = injector.instanceOf[AccessibilityStatement]
  "The accessibility statement page" when {

    lazy val fakeUri: String = "/vat-through-software/vat-overview"

    "configured to be partially compliant" should {

      lazy val view = accessibilityStatementView(fakeUri, fullyCompliant = false)
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
            "The menu toggle in the banner on a screen that has been reduced from full size disappears when clicked. " +
            "The language selector and back links are currently not bypassed using the skip link and " +
              "cannot be bypassed with assistive technology. " +
            "The form element legend has been incorrectly nested. This may make it more difficult for assistive " +
              "technologies to read the content accurately and without crashing. " +
            "The ‘aria-describedby’ attribute is not pointing to an element in the same document. This may cause " +
              "assistive technologies to behave unexpectedly. " +
            "The ‘hints and error’ hints have been created with incorrect markup. This may cause assistive " +
              "technologies to behave unexpectedly. " +
          "‘Tooltip’ has been incorrectly included in the error summary. This may cause assistive technologies " +
              "to behave unexpectedly. " +
          "There is no keyboard focus on radio buttons when the user chooses high contrast mode. This may " +
              "make the screen difficult to use for keyboard only users. " +
          "The ‘yes/no’ option has been repeated in the HTML code unnecessarily which may cause errors for " +
              "assistive technologies. " +
          "The error summary in some pages featuring currency inputs does not automatically link to " +
              "the currency input field. " +
          "There is a duplicate form group ID on the ‘How are the business’s VAT accounts prepared?’ page. This " +
              "may cause assistive technologies to behave unexpectedly. " +
          "Radio button hints on the ‘How are the business’s VAT accounts prepared?’ page have been incorrectly " +
            "marked up and may not work correctly with assistive technologies. " +
          "The input mode for digits and dates on pages that have whole number input fields has not been set to " +
              "numeric. This may make it more difficult if using a mobile device. " +
          "Error messages regarding years are not clear. This may cause confusion if the user has not " +
            "input a 4-digit year. " +
          "The navigation landmarks that are used by assistive technologies are not correctly placed in " +
            "the ‘Your business details page’. This may not work correctly with assistive technologies. " +
          "The HTML markup code is not correct on the ‘We’ve had a problem delivering mail to this address’ page. " +
            "This may cause assistive technologies to behave unexpectedly. " +
          "The HTML markup code is not correct on the ‘You have submitted a VAT Return’ page. This may cause " +
            "assistive technologies to behave unexpectedly. " +
          "The Welsh language link on the ‘You have confirmed the business address’ page does not work correctly. " +
          "The ‘Continue’ button on the ‘You have confirmed the business address’ page does not work as intended. " +
          "The page ‘We’ve had a problem delivering mail to this address’ has incorrect page markup. This may " +
              "cause assistive technologies to behave unexpectedly. " +
          "The ‘Submit VAT Returns’ page has errors with the way that hints were implemented for assisted " +
              "technologies. This may lead to unnecessary noise when using a screen reader. " +
          "The ‘Submit VAT Returns’ page has errors with the way that the HTML code elements were nested. This may " +
            "cause assistive technologies to behave unexpectedly. " +
          "The keyboard focus for the second button is the wrong colour on the ‘Submit VAT Returns’ page’. " +
          "The ‘Your business details’ page contains some hidden elements that may not be correctly implemented " +
              "and may not be ignored. This may cause assistive technologies to behave unexpectedly."
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
            "All of these accessibility problems will be fixed at the end of May 2021."
        }

        "has a bullet list of service difficulties" in {
          elementText("#non-compliance > ul") shouldBe
           "The menu toggle in the banner on a screen that has been reduced from full size disappears when clicked. " +
             "This does not meet WCAG 2.1: Understanding Success Criterion 1.3.1: Info and Relationships (A). " +
          "The language selector and back links are currently not bypassed using the skip link or possible to bypass " +
          "with assistive technology. This doesn’t meet WCAG 2.1 failure: Understanding Success " +
             "Criterion 2.4.1: Bypass Blocks (A). " +
          "The form element legend has been incorrectly nested. This may make it more difficult for assistive " +
             "technologies to read the content accurately and without crashing. This does not meet WCAG 2.1: " +
             "Understanding Success Criterion 4.1.1: Parsing (A). " +
          "The ‘aria-describedby’ attribute is not pointing to an element in the same document. This may cause " +
             "assistive technologies to behave unexpectedly. This does not meet WCAG 2.1: Understanding Success " +
             "Criterion 4.1.1: Parsing (A). " +
          "The hints and error hints have been created with incorrect markup. This may cause assistive technologies " +
             "to behave unexpectedly. This does not meet WCAG 2.1: Understanding Success Criterion " +
             "3.3.2: Labels or Instructions (A). " +
          "‘Tooltip’ has been incorrectly included in the error summary. This may cause assistive technologies to " +
             "behave unexpectedly. This does not meet WCAG 2.1: Understanding Success Criterion 4.1.1: Parsing. " +
          "There is no keyboard focus on radio buttons when the user chooses high contrast mode. This may make the " +
             "screen difficult to user for keyboard only users. This does not meet WCAG 2.1: 2.4.7: Focus visible. " +
          "The ‘yes/no’ option has been repeated in the HTML code unnecessarily which may cause errors for assistive " +
             "technologies. This doesn’t meet WCAG 2.1: Understanding Success Criterion 4.1.1: Parsing. " +
          "The error summary in some pages featuring currency inputs does not automatically link to the currency " +
             "input field. This does not meet WCAG 2.1: Understanding Success Criterion 3.3.1: Error Identification. " +
          "There is a duplicate form group ID on the ‘How are the business’s VAT accounts prepared?’ page. This may " +
             "cause assistive technologies to behave unexpectedly. This does not meet WCAG 2.1: Understanding " +
             "Success Criterion 4.1.1: Parsing. " +
          "Radio button hints on the ‘How are the business’s VAT accounts prepared?’ page have been incorrectly " +
             "marked up and may not work correctly with assistive technologies. This is a failure of WCAG 2.1: " +
             "Understanding Success Criterion 3.3.2: Labels or Instructions. " +
          "The inputmode for digits and dates on pages that have whole number input fields has not been set to " +
             "numeric, this may make it more difficult if using a mobile device. This does not meet WCAG 2.1: " +
             "Understanding Success Criterion 1.3.5: Identify Input Purpose (AA). " +
          "Error messages regarding years are not clear. This may cause confusion if the user has not input a 4 " +
             "digit year, this does not meet WCAG 2.1: Understanding Success Criterion 2.4.4: " +
             "Link Purpose (In Context). " +
          "The navigation landmarks that are used by assistive technologies are not correctly placed in the " +
          "‘Your business details’ page’. This may not work correctly with assistive technologies. This does not " +
             "meet WCAG 2.1: Understanding Success Criterion 2.4.1: Bypass Blocks. " +
          "The HTML markup code is not correct on the ‘We’ve had a problem delivering mail to this address’ page. " +
             "This may cause assistive technologies to behave unexpectedly. This does not meet WCAG 2.1 failure: " +
             "Understanding Success Criterion 4.1.1: Parsing. " +
          "The HTML markup code is not correct on the ‘You have submitted a VAT Return’ page. This may cause " +
             "assistive technologies to behave unexpectedly. This does not meet WCAG 2.1: Understanding Success " +
             "Criterion 4.1.1: Parsing. " +
          "The Welsh language link on the ‘You have confirmed the business address’ page does not work correctly. " +
             "This does not meet WCAG 2.1: Understanding Success Criterion 2.4.4: Link Purpose (In Context). " +
          "The page ‘We’ve had a problem delivering mail to this address’ has incorrect page markup. This may cause " +
             "assistive technologies to behave unexpectedly. This does not meet WCAG 2.1: Understanding Success " +
             "Criterion 3.3.2: Labels or Instructions. " +
          "The ‘Submit VAT Returns’ page has errors with the way that hints were implemented for assisted " +
             "technologies. This may lead to unnecessary noise when using a screen reader. This does not meet " +
             "WCAG 2.1: Understanding Success Criterion 3.3.2: Labels or Instructions. " +
          "The ‘Submit VAT Returns’ page has errors with the way that the HTML code elements were nested. This may " +
             "cause assistive technologies to behave unexpectedly. This does not meet WCAG 2.1 failure: " +
             "Understanding Success Criterion 4.1.1: Parsing. " +
          "On the ‘Submit VAT Returns’ page the keyboard focus for the second button is the wrong colour. This does " +
             "not meet WCAG 2.1: 2.4.7 – Focus visible. " +
          "The ‘Your business details’ page contains some hidden elements that may not be correctly implemented " +
             "and may not be ignored. This may cause assistive technologies to behave unexpectedly. This does not " +
             "meet WCAG 2.1 failure: Understanding Success Criterion 4.1.2: Name, Role, Value."
        }
      }

      "have a section on how we tested this service" which {

        "has the correct heading" in {
          elementText("#how-we-tested > h2") shouldBe "How we tested this service"
        }

        "has the correct first paragraph" in {
          elementText("#how-we-tested > p:nth-of-type(1)") shouldBe
            "The service was last tested on 29 September 2020 and was checked for compliance with WCAG 2.1 AA."
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
            "This page was prepared on 20 September 2019. It was last updated on 22 October 2020."
        }
      }
    }

    "configured to be fully compliant" should {

      lazy val view = accessibilityStatementView(fakeUri, fullyCompliant = true)
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
