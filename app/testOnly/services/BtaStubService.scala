/*
 * Copyright 2019 HM Revenue & Customs
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

package testOnly.services

import javax.inject.Inject

import play.api.http.Status._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html
import testOnly.connectors.BtaStubConnector
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BtaStubService @Inject()(btaStubConnector: BtaStubConnector) {

  def getPartial(partialUrl: String)(implicit request: Request[AnyContent]): Future[Html] = {
    btaStubConnector.getPartial(partialUrl).flatMap { result =>
      handlePartial(result)
    }
  }

  private[services] def handlePartial(partial: HtmlPartial): Future[Html] = {
    partial match {
      case Success(_, content) => Future.successful(content)
      case Failure(Some(UNAUTHORIZED), _) => Future.successful(Html("User is unauthorised"))
      case Failure(Some(FORBIDDEN), _) => Future.successful(Html("User is forbidden"))
      case Failure(_, _) => Future.successful(Html("Alternative content"))
    }
  }
}
