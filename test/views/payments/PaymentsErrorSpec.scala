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

package views.payments

import java.time.LocalDate

import models.User
import models.viewModels.OpenPaymentsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class PaymentsErrorSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val tryLater = "#errorDetails p:nth-of-type(1)"
    val errorDetail = "#errorDetails"
    val reasonableExcuseDetail = "#reasonableExcuseDetail"
    val hiddenIconText = "#reasonableExcuseDetail span:nth-of-type(1)"

  }

  private val user = User("1111")
  val noPayment = Seq()
  val payment = Seq(
    OpenPaymentsModel(
      "Return",
      543.21,
      LocalDate.parse("2000-04-08"),
      LocalDate.parse("2000-01-01"),
      LocalDate.parse("2000-03-31"),
      "#001"
    )
  )

  "Rendering the no payments page" should {

    lazy val view = views.html.payments.paymentsError(user)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "VAT payments"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "We can't let you pay here right now"
    }

    "have the correct reload/try again message" in {
      elementText(Selectors.tryLater) shouldBe "Try reloading the page or coming back later."
    }

    lazy val errorDetail = element(Selectors.errorDetail)

    s"have the correct payment service details text" in {
      errorDetail.select("p:nth-of-type(2)").text shouldBe "You can also use the HMRC payments service if you have your VAT details to hand."
    }

    s"have the correct payment service href" in {
      errorDetail.select("p:nth-of-type(2) a").attr("href") shouldBe "#"
    }

    s"have the correct contact service details text" in {
      errorDetail.select("p:nth-of-type(3)").text shouldBe
        "If you're close to a payment deadline and worried you'll miss it, please contact HMRC as soon as possible."
    }

    s"have the correct contact service href" in {
      errorDetail.select("p:nth-of-type(3) a").attr("href") shouldBe "#"
    }

    "have the correct hidden warning icon text" in {
      elementText(Selectors.hiddenIconText) shouldBe "Warning"
    }

    lazy val reasonableExcuseDetail = element(Selectors.reasonableExcuseDetail)

    s"have the correct reasonable excuse detail text" in {
      reasonableExcuseDetail.select("p:nth-of-type(1)").text shouldBe
        "Issues with HMRC's online services are a reasonable excuse when appealing a penalty."
    }

    s"have the correct reasonable excuse href" in {
      reasonableExcuseDetail.select("p:nth-of-type(1) a").attr("href") shouldBe "#"
    }
  }

}
