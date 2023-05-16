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

package common

object MessageLookup {

  object SessionTimeout {
    val title: String = "Your session has timed out"
    val instructions: String = "To view your VAT summary, you'll have to sign in using your Government Gateway ID."
  }

  object Unauthorised {
    val title: String = "Unauthorised access"
    val instructions: String = "Here are some instructions about what you should do next."
  }

  object InsolventError {
    val title: String = "Sorry, you cannot access this service - Manage your VAT account - GOV.UK"
    val heading: String = "Sorry, you cannot access this service"
    val message: String = "Your business has been declared insolvent."
    val buttonText: String = "Go to your business tax account"
    val signOutLink: String = "Sign out"
  }

  object PaymentHistoryMessages {
    val insetText: String = "If you cannot see your all of your client’s history, " +
      "you may be able to access more through your HMRC online services for agents account. You’ll need to sign in separately."
  }
}
