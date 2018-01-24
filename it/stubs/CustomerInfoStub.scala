
package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.Json

object CustomerInfoStub extends WireMockMethods {

  private val financialDataUri = "/customer-information/vat/([0-9]+)"

  def stubCustomerInfo: StubMapping = {
    when(method = GET, uri = financialDataUri)
      .thenReturn(status = OK, body = customerInfo)
  }

  private val customerInfo = Json.parse(
    """{
      |    "organisationDetails" : {
      |      "organisationName" : "Cheapo Clothing Ltd",
      |      "individualName" : {
      |        "title" : "0001",
      |        "firstName" : "Vincent",
      |        "middleName" : "Victor",
      |        "lastName" : "Vatreturn"
      |      },
      |      "tradingName" : "Cheapo Clothing",
      |      "mandationStatus" : "1",
      |      "registrationReason" : "0001",
      |      "effectiveRegistrationDate" : "2017-08-21",
      |      "businessStartDate" : "2017-01-01"
      |    }
      |  }""".stripMargin
  )

}
