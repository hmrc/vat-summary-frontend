
package testOnly.connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.libs.json.{JsValue, Json}
import testOnly.models.{DataModel, SchemaModel}
import testOnly.stubs.DynamicStub
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import play.api.test.Helpers._

import scala.concurrent.Future


class DynamicStubConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping
    val connector: DynamicStubConnector = app.injector.instanceOf[DynamicStubConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val exampleService: String = "vat-api"
  }

  "Calling clearStub" when {

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.clearStubOkResponse()

        setupStubs()
        val result: Future[HttpResponse] = connector.clearStub(exampleService)

        await(result).status shouldBe OK
      }
    }
  }

  "Calling populateStub" when {

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.populateStubOkResponse()

        setupStubs()
        val result: Future[HttpResponse] = connector.populateStub(DataModel("/test", None, "GET", OK, None), exampleService)

        await(result).status shouldBe OK
      }
    }
  }

  "Calling populateSchema" when {

    val jsonSchema: JsValue = Json.parse(
           """{
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

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.populateSchemaOkResponse()

        setupStubs()
        val result: Future[HttpResponse] = connector.populateSchema(SchemaModel("getFinancialData", "/test", "GET", jsonSchema, None), exampleService)

        await(result).status shouldBe OK
      }
    }
  }

  "Calling clearSchemas" when {

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.clearSchemasOkResponse()

        setupStubs()
        val result: Future[HttpResponse] = connector.clearSchemas(exampleService)

        await(result).status shouldBe OK
      }
    }
  }

}
