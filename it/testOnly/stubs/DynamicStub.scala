
package testOnly.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._

object DynamicStub extends WireMockMethods {

  private val clearDataUri = "/setup/all-data"
  private val clearSchemasUri = "/setup/all-schemas"
  private val populateDataUri = "/setup/data"
  private val populateSchemaUri = "/setup/schema"

  def clearStubOkResponse(): StubMapping = when(method = DELETE, uri = clearDataUri)
    .thenReturn(OK)

  def populateStubOkResponse(): StubMapping = when(method = POST, uri = populateDataUri)
    .thenReturn(OK)

  def clearSchemasOkResponse(): StubMapping = when(method = DELETE, uri = clearSchemasUri)
    .thenReturn(OK)

  def populateSchemaOkResponse(): StubMapping = when(method = POST, uri = populateSchemaUri)
    .thenReturn(OK)

}
