/*
 * Copyright 2017 HM Revenue & Customs
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

package common

object MessageLookup {

  object SessionTimeout {
    val title: String = "Your session has timed out"
    val instructions: String = "To manage your VAT account, you'll have to sign in using your Government Gateway ID."
  }

  object Unauthorised {
    val title: String = "Unauthorised access"
    val instructions: String = "Here are some instructions about what you should do next."
  }
}
