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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.NextPaymentSection

class NextPaymentSectionTemplateSpec extends ViewBaseSpec {

  val nextPaymentSection: NextPaymentSection = injector.instanceOf[NextPaymentSection]

  "The nextPaymentSection template" when {
    object Selectors {
      val nextPaymentDueHeading = "h2:nth-of-type(1)"
      val nextPaymentDate = "p:nth-of-type(1)"
      val viewPaymentButton = "a:nth-of-type(1)"
      val portalLink = "a"
      val checkPOAlinktext = "#poa-schedule-of-payment"
      val checkPOAlink = "#poa-schedule-of-payment"
    }

    "there is a payment to display" when {

      "payment is not overdue" should {
        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = false,
          isPoaActiveForCustomer = false
        )
        lazy val viewAsString = view.toString
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display the 'Next payment due' heading" in {
          elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
        }

        "display the due date of the payment" in {
          elementText(Selectors.nextPaymentDate) shouldBe "8 March 2017"
        }

        "use non breaking spaces to display the due date of the payment" in {
          viewAsString.contains("Next payment due 8\u00a0March\u00a02017")
        }

        "display the 'View payment details' button" in {
          elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
        }

        "not display overdue flag" in {
          elementExtinct(".govuk-tag--red")
        }
      }

      "payment is overdue" should {

        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = true,
          isPoaActiveForCustomer = false
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display overdue flag" in {
          elementText(".govuk-tag--red")shouldBe "overdue"
        }
      }
    }

    "there is no payment to display" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = false,
        isHybridUser = false,
        isOverdue = false,
        isPoaActiveForCustomer = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the 'No payments due right now' message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "No payments due right now"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }
    }

    "there is an error retrieving the payment" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = true,
        isHybridUser = false,
        isOverdue = false,
        isPoaActiveForCustomer = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the error message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "Sorry, there is a problem with the service. Try again later."
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }

    }

    "there are multiple payments to display" should {

      lazy val view = nextPaymentSection(Some("2"),
        hasMultiple = true,
        isError = false,
        isHybridUser = false,
        isOverdue = false,
        isPoaActiveForCustomer = true)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Payments due"
      }

      "display the 'payments due' message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "You have 2 payments due"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }
    }

    "the user is hybrid" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = false,
        isHybridUser = true,
        isOverdue = false,
        isPoaActiveForCustomer = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)
      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display a link to the portal" in {
        element(Selectors.portalLink).attr("href") shouldBe controllers.routes.PortalController.hybridWYO.url
      }

      "have the correct link text" in {
        elementText(Selectors.portalLink) shouldBe "Check what you owe and make a payment (opens in a new tab)"
      }
    }

    "there is a payment on account schedule link to display when feature switch is enabled" when {
      mockConfig.features.poaActiveFeatureEnabled(true)
      "payment on account link is displayed when POAActiveUntil is valid" should {
        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = false,
          isPoaActiveForCustomer = true
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)
        println( " ******** - 1 " + view.body)
        "have the correct link text for poa if POAActiveUntil is true and POAActiveFeature is enabled" in {
          elementText(Selectors.checkPOAlinktext) shouldBe "Check your payments on account schedule"
        }

        "display the due date of the payment" in {
          val linkElement = element(Selectors.checkPOAlink)
          linkElement.attr("href") shouldBe controllers.routes.PaymentsOnAccountController.show.url
        }
      }

      "payment on account link is not displayed when POAActiveUntil is false" should {
        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = true,
          isPoaActiveForCustomer = false
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)
        "display overdue flag" in {
          document.select(Selectors.checkPOAlinktext) should be(empty)
        }
      }
    }

    "there is no payment on account schedule link to display when feature switch is disabled" when {
      mockConfig.features.poaActiveFeatureEnabled(false)
      "payment on account link is not displayed" should {
        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = false,
          isPoaActiveForCustomer = true
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)
        "have the poa link text  is not displayed if POAActiveUntil is true" in {
          document.select(Selectors.checkPOAlinktext) should be(empty)
        }
      }
    }
  }
}