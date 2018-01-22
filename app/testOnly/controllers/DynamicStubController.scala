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

package testOnly.controllers

import com.google.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent}
import testOnly.connectors.DynamicStubConnector
import testOnly.models.DataModel
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future
import scala.util.{Success, Try}

class DynamicStubController @Inject()(dynamicStubConnector: DynamicStubConnector)
  extends FrontendController {

  val populateStub: Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    Try(request.body.as[DataModel]) match {
      case Success(dataModel) =>
        dynamicStubConnector.populateStub(dataModel).map { response =>
          response.status match {
            case OK => Ok(s"Successfully populated stub with $dataModel")
            case _ => InternalServerError(s"Unable to populate stub with $dataModel")
          }
        }
      case _ => Future.successful(BadRequest("Unable to convert json to model"))
    }
  }

  val clearStub: Action[AnyContent] = Action.async { implicit request =>
    dynamicStubConnector.clearStub().map(
      response => response.status match {
        case OK => Ok(s"Successfully cleared all stub data")
        case _ => InternalServerError(s"Unable to clear stub ${response.body}")
      }
    )
  }
}
