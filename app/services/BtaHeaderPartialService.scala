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

package services

import javax.inject.Inject

import connectors.BtaHeaderPartialConnector
import play.api.Logger
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BtaHeaderPartialService @Inject()(val btaHeaderPartialConnector: BtaHeaderPartialConnector) {

  def btaHeaderPartial()(implicit request: Request[AnyContent]): Future[Html] = {
    btaHeaderPartialConnector.getBtaHeaderPartial().map { htmlResult =>
      if (htmlResult.body.isEmpty) {
        Logger.warn("[BtaHeaderPartialService][btaHeaderPartial] - could not retrieve BTA Header Partial")
        Html("")

      } else {
        Logger.debug("[BtaHeaderPartialService][btaHeaderPartial] - retrieved BTA Header Partial")
        htmlResult
      }
    }
  }
}
