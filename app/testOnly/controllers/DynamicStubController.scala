/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import testOnly.connectors.DynamicStubConnector
import testOnly.models.{DataModel, SchemaModel}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class DynamicStubController @Inject()(dynamicStubConnector: DynamicStubConnector,
                                      mcc: MessagesControllerComponents,
                                      implicit val ec: ExecutionContext) extends FrontendController(mcc) {

  def populateStub(serviceName: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    Try(request.body.as[DataModel]) match {
      case Success(dataModel) =>
        dynamicStubConnector.populateStub(dataModel, serviceName).map { response =>
          response.status match {
            case OK => Ok(s"Successfully populated $serviceName stub with $dataModel")
            case _ => InternalServerError(s"Unable to populate $serviceName stub with $dataModel")
          }
        }
      case _ => Future.successful(BadRequest("Unable to convert json to model"))
    }
  }

  def populateSchema(serviceName: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    Try(request.body.as[SchemaModel]) match {
      case Success(schemaModel) =>
        dynamicStubConnector.populateSchema(schemaModel, serviceName).map { response =>
          response.status match {
            case OK => Ok(s"Successfully populated $serviceName schema with $schemaModel")
            case _ => InternalServerError(s"Unable to populate $serviceName schema with $schemaModel")
          }
        }
      case _ => Future.successful(BadRequest("Unable to convert json to model"))
    }
  }

  def clearSchemas(serviceName: String): Action[AnyContent] = Action.async { implicit request =>
    dynamicStubConnector.clearSchemas(serviceName).map(
      response => response.status match {
        case OK => Ok(s"Successfully cleared all $serviceName schemas")
        case _ => InternalServerError(s"Unable to clear $serviceName schemas ${response.body}")
      }
    )
  }

  def clearStub(serviceName: String): Action[AnyContent] = Action.async { implicit request =>
    dynamicStubConnector.clearStub(serviceName).map(
      response => response.status match {
        case OK => Ok(s"Successfully cleared all $serviceName stub data")
        case _ => InternalServerError(s"Unable to clear $serviceName stub ${response.body}")
      }
    )
  }
}
