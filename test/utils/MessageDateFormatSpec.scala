/*
 * Copyright 2025 HM Revenue & Customs
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

package utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.test.Helpers.stubMessagesApi

import java.time.LocalDate

class MessageDateFormatSpec extends AnyWordSpecLike with Matchers {

  private val date = LocalDate.parse("2025-01-31")
  private val messagesApi: MessagesApi = stubMessagesApi()

  private implicit val englishMessages: Messages = MessagesImpl(Lang("en"), messagesApi)
  private val welshMessages: Messages = MessagesImpl(Lang("cy"), messagesApi)

  "MessageDateFormat.format" should {
    "format dates in English short month style" in {
      MessageDateFormat.format(date) shouldBe "31 Jan 2025"
    }

    "format dates in Welsh short month style" in {
      MessageDateFormat.format(date)(welshMessages) shouldBe "31 Ion 2025"
    }
  }

  "MessageDateFormat.formatLong" should {
    "format dates in English long month style" in {
      MessageDateFormat.formatLong(date) shouldBe "31 January 2025"
    }

    "format dates in Welsh long month style" in {
      MessageDateFormat.formatLong(date)(welshMessages) shouldBe "31 Ionawr 2025"
    }
  }

  "MessageDateFormat.formatNoText" should {
    "format dates without month text" in {
      MessageDateFormat.formatNoText(date) shouldBe "31 01 2025"
    }
  }
}
