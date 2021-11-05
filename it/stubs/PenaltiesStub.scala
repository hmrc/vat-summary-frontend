
package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.libs.json.JsValue

object PenaltiesStub extends WireMockMethods {

  private val penaltiesServiceUrl = "/vat/penalties/summary/([0-9]+)"

  def stubPenaltiesSummary(status: Int, response: JsValue): StubMapping = {
    when(method = GET, uri = penaltiesServiceUrl)
      .thenReturn(status = status, body = response)
  }


}
