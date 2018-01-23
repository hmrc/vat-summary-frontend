
package stubs

import helpers.WireMockMethods

object FinancialDataStub extends WireMockMethods {

  private val financialDataUrl = "/financial-transactions/vrn/([0-9]+)"
  private val dateRegex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"



}
