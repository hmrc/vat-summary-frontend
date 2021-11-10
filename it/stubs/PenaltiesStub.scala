
package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.libs.json.JsValue

object PenaltiesStub extends WireMockMethods {

  private val penaltiesServiceUrl: String => String = vrn => s"/penalties/vat/penalties/summary/$vrn"

  def stubPenaltiesSummary(status: Int, response: JsValue, vrn: String): StubMapping = {
    when(method = GET, uri = penaltiesServiceUrl(vrn))
      .thenReturn(status = status, body = response)
  }


}
