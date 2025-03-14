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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.io.Source

class MessagesFileSpec extends AnyWordSpec with Matchers {

  "The messages.en file" should {

    "not contain invalid characters" in {
      val filePath = "conf/messages"
      val forbiddenChars = Set('\'')

      val fileContent = Source.fromFile(filePath).getLines().mkString("\n")

      forbiddenChars.foreach { char =>
        withClue(s"File contains forbidden character: '$char'") {
          fileContent must not include char.toString
        }
      }
    }
  }
}
