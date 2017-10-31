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

package controllers.auth

import cats.implicits._
import controllers.auth.AuthPredicate.{AuthPredicate, Success}
import play.api.mvc.{Result, Results}
import uk.gov.hmrc.http.SessionKeys.{authToken, lastRequestTimestamp}

import scala.concurrent.Future

object AuthPredicates extends Results {

  lazy val unauthorisedRoute: Result = Redirect(controllers.routes.ErrorsController.unauthorised())
  lazy val timeoutRoute: Result = Redirect(controllers.routes.ErrorsController.sessionTimeout())

  private[auth] val enrolledPredicate: AuthPredicate = _ => user =>
    if (user.Vrn.isDefined) {
      Right(Success)
    } else {
      Left(Future.successful(unauthorisedRoute))
    }

  private[auth] val timeoutPredicate: AuthPredicate = request => _ =>
    if(request.session.get(lastRequestTimestamp).isDefined && request.session.get(authToken).isEmpty) {
      Left(Future.successful(timeoutRoute))
    } else {
      Right(Success)
    }

  val enrolledUserPredicate: AuthPredicate = timeoutPredicate |+| enrolledPredicate
}
