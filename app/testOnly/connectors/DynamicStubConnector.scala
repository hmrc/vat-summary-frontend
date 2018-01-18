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

package testOnly.connectors

import javax.inject.Inject

import testOnly.TestOnlyAppConfig
import testOnly.models.DataModel
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future

class DynamicStubConnector @Inject()(val http: HttpClient,
                                     val appConfig: TestOnlyAppConfig) {

  def populateStub(dataModel: DataModel)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/data"
    http.POST[DataModel, HttpResponse](url, dataModel)
  }

  def clearStub()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/all-data"
    http.DELETE[HttpResponse](url)
  }

}
