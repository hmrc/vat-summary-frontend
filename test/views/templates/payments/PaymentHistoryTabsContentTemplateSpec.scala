/*
 * Copyright 2020 HM Revenue & Customs
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

package views.templates.payments

import java.time.LocalDate

import models.User
import models.payments.ReturnDebitCharge
import models.viewModels.PaymentsHistoryModel
import play.twirl.api.Html
import views.html.templates.payments.{PaymentsHistoryCharge, PaymentsHistoryTabsContent}
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsContentTemplateSpec extends TemplateBaseSpec {

  val paymentsHistoryCharge: PaymentsHistoryCharge = injector.instanceOf[PaymentsHistoryCharge]
  val paymentsHistoryTabsContent: PaymentsHistoryTabsContent = injector.instanceOf[PaymentsHistoryTabsContent]

  val currentYear = 2018
  val previousYear = 2017
  val exampleAmount = 100
  val singleYear: Seq[Int] = Seq(currentYear)
  val multipleYears: Seq[Int] = Seq(currentYear, previousYear)
  implicit val user: User = User("999999999")

  val transaction: PaymentsHistoryModel = PaymentsHistoryModel(
    chargeType = ReturnDebitCharge,
    taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
    taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
    amount = exampleAmount,
    clearedDate = Some(LocalDate.parse(s"2018-03-01"))
  )

  val sectionExampleWithPayment: String =
    s"""
      |<section id="$currentYear" class="tabcontent" role="tabpanel">
      |  <h2 class="heading-medium">$currentYear</h2>
      |  <table>
      |    <thead>
      |      <tr>
      |        <th scope="col">
      |          <div class="visuallyhidden">Payment received</div>
      |        </th>
      |        <th scope="col">
      |          <div class="visuallyhidden">Description</div>
      |        </th>
      |        <th class="numeric" scope="col">
      |          <div class="visuallyhidden">Amount</div>
      |        </th>
      |      </tr>
      |    </thead>
      |    <tbody>
      |      ${paymentsHistoryCharge(transaction)}
      |    </tbody>
      |  </table>
      |</section>
    """.stripMargin

  def sectionExampleWithoutPayment(year: Int, javascriptEnabled: Boolean = true): String = {
    val sectionAttributes: String =
      if(javascriptEnabled) {
        s"""id="$year" class="tabcontent" role="tabpanel""""
      } else {
        s"""id="nonJS-$year""""
      }
    s"""
      |<section $sectionAttributes>
      |  <h2 class="heading-medium">$year</h2>
      |  <p>You have not made or received any payments using the new VAT service this year.</p>
      |</section>
    """.stripMargin
  }

  "The payment history tabs content template" when {

    "javascript is enabled" when {

      "the showPreviousPaymentsTab boolean is set to false" when {

        "there is one year" when {

          "there are no transactions" should {

            "render the correct HTML" in {

              val expectedMarkup = Html(sectionExampleWithoutPayment(currentYear))

              val result = paymentsHistoryTabsContent(
                singleYear, Seq.empty, showPreviousPaymentsTab = false, javascriptEnabled = true
              )

              formatHtml(result) shouldBe formatHtml(expectedMarkup)
            }
          }

          "there are some transactions" should {

            "render the correct HTML" in {

              val expectedMarkup = Html(sectionExampleWithPayment)

              val result = paymentsHistoryTabsContent(
                singleYear, Seq(transaction), showPreviousPaymentsTab = false, javascriptEnabled = true
              )

              formatHtml(result) shouldBe formatHtml(expectedMarkup)
            }
          }
        }

        "there are two years" should {

          "there are no transactions" should {

            "render the correct HTML" in {
              val expectedMarkup = Html(
                sectionExampleWithoutPayment(currentYear) + sectionExampleWithoutPayment(previousYear)
              )

              val result = paymentsHistoryTabsContent(
                multipleYears, Seq.empty, showPreviousPaymentsTab = false, javascriptEnabled = true
              )

              formatHtml(result) shouldBe formatHtml(expectedMarkup)
            }
          }

          "there are some transactions" should {

            "render the correct HTML" in {

              val expectedMarkup = Html(sectionExampleWithPayment + sectionExampleWithoutPayment(previousYear))

              val result = paymentsHistoryTabsContent(
                multipleYears, Seq(transaction), showPreviousPaymentsTab = false, javascriptEnabled = true
              )

              formatHtml(result) shouldBe formatHtml(expectedMarkup)
            }
          }
        }
      }

      "the showPreviousPaymentsTab boolean is set to true" should {

        "render the correct HTML" in {

          val expectedMarkup = Html(
            sectionExampleWithoutPayment(currentYear) +
            s"""
              |<section id="previous-payments" class="tabcontent" role="tabpanel">
              |  <h2 class="heading-medium">Previous payments</h2>
              |  <p>
              |    You can
              |    <a href="${mockAppConfig.portalNonHybridPreviousPaymentsUrl(user.vrn)}">
              |      view your previous payments (opens in new tab)
              |    </a>
              |    if you made payments before joining Making Tax Digital.
              |  </p>
              |</section>
            """.stripMargin
          )

          val result = paymentsHistoryTabsContent(
            singleYear, Seq.empty, showPreviousPaymentsTab = true, javascriptEnabled = true
          )

          formatHtml(result) shouldBe formatHtml(expectedMarkup)
        }
      }
    }

    "javascript is disabled" should {

      "render the correct HTML" in {

        val expectedMarkup = Html(
          s"""
             |<p>
             |  <a href="#nonJS-$currentYear">$currentYear</a>
             |</p>
          """.stripMargin +
          sectionExampleWithoutPayment(currentYear, javascriptEnabled = false) +
          s"""
            |<div>
            |  <a href="#top" class="back-to-top-link">
            |    <svg class="arrow input--radio-inline" width="13" height="15" viewBox="0 -5 13 15">
            |      <path fill="currentColor" d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"></path>
            |    </svg><span>Back to top</span>
            |  </a>
            |</div>
          """.stripMargin
        )

        val result = paymentsHistoryTabsContent(
          singleYear, Seq.empty, showPreviousPaymentsTab = false, javascriptEnabled = false
        )

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
