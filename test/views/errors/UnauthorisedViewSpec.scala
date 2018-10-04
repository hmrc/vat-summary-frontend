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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class UnauthorisedViewSpec extends ViewBaseSpec {

  "Rendering the unauthorised page" should {

    object Selectors {
      val pageHeading = "#content h1"
      val signUpWithSoftware = "#content p:nth-of-type(1)"
      val signInCorrectCredentials = "#content p:nth-of-type(2)"
      val signOut = "#content p:nth-of-type(3)"
      val signOutLink = "#content p:nth-of-type(3) a"
    }

    lazy val view = views.html.errors.unauthorised()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "You are not authorised to use this service"
    }

    "have a the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "You are not authorised to use this service"
    }

    "have the correct instructions to sign up" in {
      elementText(Selectors.signUpWithSoftware) shouldBe "You need to sign up to use software to submit your VAT Returns."
    }

    "have the correct instructions to sign in" in {
      elementText(Selectors.signInCorrectCredentials) shouldBe "If you have already signed up, you need to sign in with the correct Government Gateway details."
    }

    "have the correct sign out text" in {
      elementText(Selectors.signOut) shouldBe "Sign out"
    }

    "have the correct sign out link" in {
      element(Selectors.signOutLink).attr("href") shouldBe "/vat-through-software/sign-out?authorised=false"
    }

  }
}
