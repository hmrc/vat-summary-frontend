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

package testOnly.controllers

import config.AppConfig
import controllers.ControllerBaseSpec
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import testOnly.TestOnlyAppConfig
import testOnly.connectors.DynamicStubConnector
import testOnly.models.{DataModel, SchemaModel}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class DynamicStubControllerSpec extends ControllerBaseSpec {

  override implicit val mockAppConfig: AppConfig = {
    app.injector.instanceOf[TestOnlyAppConfig]
  }

  private trait Test {

    protected lazy val mockConnector: DynamicStubConnector = mock[DynamicStubConnector]
    protected lazy val controller = new DynamicStubController(mockConnector)

    protected def setup(): Unit

  }

  "Calling populateSchema with a valid SchemaModel" when {

    val invalidSchemaJson: JsValue = Json.parse(
      """{}"""
    )

    val jsonSchema: JsValue = Json.parse("""{
                       |   "$schema": "http://json-schema.org/draft-04/schema#",
                       |   "title": "test API schemea",
                       |   "description": "A test schema",
                       |   "type": "object",
                       |
                       |   "properties": {
                       |
                       |      "id": {
                       |         "description": "The unique identifier for a product",
                       |         "type": "integer"
                       |      },
                       |
                       |      "name": {
                       |         "description": "Name",
                       |         "type": "string"
                       |      },
                       |
                       |      "price": {
                       |         "type": "number",
                       |         "minimum": 0,
                       |         "exclusiveMinimum": true
                       |      }
                       |   },
                       |
                       |   "required": ["id", "name", "price"]
                       |}""".stripMargin)

    "the connector returns a 200 OK response" should {

      "return a 200 OK response" in new Test {
        val schema: SchemaModel = SchemaModel("getFinancialData", "/test", "GET", jsonSchema, None)

        override def setup(): Unit = {
          (mockConnector.populateSchema(_: SchemaModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(schema, *, *, *)
            .returning(HttpResponse(OK))
        }

        setup()
        val result: Future[Result] = controller.populateSchema("")(fakeRequest.withBody(Json.toJson(schema)))

        status(result) shouldBe OK
      }
      "the connector returns an error response (400)" should {

        "return a 500 INTERNAL SERVER ERROR" in new Test {
          val schema: SchemaModel = SchemaModel("getFinancialData", "/test", "GET", invalidSchemaJson, None)

          override def setup(): Unit = {
            (mockConnector.populateSchema(_: SchemaModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(schema,*,*,*)
              .returning(HttpResponse(BAD_REQUEST))
          }

          setup()
          val result: Future[Result] = controller.populateSchema("")(fakeRequest.withBody(Json.toJson(schema)))

          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }

      "Calling populateSchema with an invalid SchemaModel" should {

        "return a 400 BAD REQUEST response" in new Test {
          override protected def setup(): Unit = {}

          val result: Future[Result] = controller.populateSchema("")(fakeRequest.withBody(Json.parse("""{"bad":"data"}""")))

          status(result) shouldBe BAD_REQUEST
        }
      }

      "Calling clearSchemas" when {

        "the connector returns a 200 OK response" should {

          "return a 200 OK response" in new Test {
            override protected def setup(): Unit = {
              (mockConnector.clearSchemas(_: String)(_: HeaderCarrier, _:ExecutionContext))
                .expects(*,*,*)
                .returning(HttpResponse(OK))
            }

            setup()
            val result: Future[Result] = controller.clearSchemas("")(fakeRequest)

            status(result) shouldBe OK
          }
        }

        "the connector returns an error response" should {

          "return a 500 INTERNAL SERVER ERROR response" in new Test {
            override protected def setup(): Unit = {
              (mockConnector.clearSchemas(_: String)(_: HeaderCarrier, _: ExecutionContext))
                .expects(*,*,*)
                .returning(HttpResponse(INTERNAL_SERVER_ERROR))
            }

            setup()
            val result: Future[Result] = controller.clearSchemas("")(fakeRequest)
            status(result) shouldBe INTERNAL_SERVER_ERROR
          }
        }
      }

    }
  }

  "Calling populateStub with a valid DataModel" when {

    "the connector returns a 200 OK response" should {

      "return a 200 OK response" in new Test {
        val data: DataModel = DataModel("/test", None,"GET", OK, None)

        override def setup(): Unit = {
          (mockConnector.populateStub(_: DataModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(data,*,*,*)
            .returning(HttpResponse(OK))
        }

        setup()
        val result: Future[Result] = controller.populateStub("")(fakeRequest.withBody(Json.toJson(data)))

        status(result) shouldBe OK
      }

    }

    "the connector returns an error response (400)" should {

      "return a 500 INTERNAL SERVER ERROR" in new Test {
        val data: DataModel = DataModel("/test", None,"GET", OK, None)

        override def setup(): Unit = {
          (mockConnector.populateStub(_: DataModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(data,*,*,*)
            .returning(HttpResponse(BAD_REQUEST))
        }

        setup()
        val result: Future[Result] = controller.populateStub("")(fakeRequest.withBody(Json.toJson(data)))

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }

  }

  "Calling populateStub with an invalid DataModel" should {

    "return a 400 BAD REQUEST response" in new Test {
      override protected def setup(): Unit = {}

      val result: Future[Result] = controller.populateStub("")(fakeRequest.withBody(Json.parse("""{"bad":"data"}""")))

      status(result) shouldBe BAD_REQUEST
    }
  }

  "Calling clearStub" when {

    "the connector returns a 200 OK response" should {

      "return a 200 OK response" in new Test {
        override protected def setup(): Unit = {
          (mockConnector.clearStub(_: String)(_: HeaderCarrier, _:ExecutionContext))
            .expects(*,*,*)
            .returning(HttpResponse(OK))
        }

        setup()
        val result: Future[Result] = controller.clearStub("")(fakeRequest)

        status(result) shouldBe OK
      }
    }

    "the connector returns an error response" should {

      "return a 500 INTERNAL SERVER ERROR response" in new Test {
        override protected def setup(): Unit = {
          (mockConnector.clearStub(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*,*,*)
            .returning(HttpResponse(INTERNAL_SERVER_ERROR))
        }

        setup()
        val result: Future[Result] = controller.clearStub("")(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
