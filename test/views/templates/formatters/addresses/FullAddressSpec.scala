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

package views.templates.formatters.addresses

import models.Address
import play.twirl.api.Html
import views.html.templates.formatters.addresses.fullAddress
import views.templates.TemplateBaseSpec

class FullAddressSpec extends TemplateBaseSpec {

  "The full address template" when {

    "all lines are present" should {

      "render all the address lines separated by line breaks" in {

        val expected = Html("line1<br/>line2<br/>line3<br/>line4<br/>postcode").toString().trim

        val result = fullAddress(Address(
          "line1",
          Some("line2"),
          Some("line3"),
          Some("line4"),
          Some("postcode"))
        ).toString().trim

        result shouldEqual expected
      }
    }

    "line 4 is missing" should {

      "render lines 1, 2, 3 & the postcode" in {

        val expected = Html("line1<br/>line2<br/>line3<br/>postcode").toString().trim

        val result = fullAddress(Address(
          "line1",
          Some("line2"),
          Some("line3"),
          None,
          Some("postcode"))
        ).toString().trim

        result shouldEqual expected
      }
    }

    "line 3 is missing" should {

      "render lines 1, 2, 4 & the postcode" in {

        val expected = Html("line1<br/>line2<br/>line4<br/>postcode").toString().trim

        val result = fullAddress(Address(
          "line1",
          Some("line2"),
          None,
          Some("line4"),
          Some("postcode"))
        ).toString().trim

        result shouldEqual expected
      }
    }

    "lines 3 and 4 are missing" should {

      "render lines 1, 2 & the postcode" in {

        val expected = Html("line1<br/>line2<br/>postcode").toString().trim

        val result = fullAddress(Address(
          "line1",
          Some("line2"),
          None,
          None,
          Some("postcode"))
        ).toString().trim

        result shouldEqual expected
      }
    }
  }

}
