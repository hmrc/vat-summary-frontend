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

package testOnly.connectors

import javax.inject.Inject
import testOnly.TestOnlyAppConfig
import testOnly.models.{DataModel, SchemaModel}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class DynamicStubConnector @Inject()(val http: HttpClient, val appConfig: TestOnlyAppConfig) {

  def populateStub(dataModel: DataModel, serviceName: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl(serviceName)}/setup/data"

    http.POST[DataModel, HttpResponse](url, dataModel)
  }

  def populateSchema(schemaModel: SchemaModel, serviceName: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl(serviceName)}/setup/schema"
    http.POST[SchemaModel, HttpResponse](url, schemaModel)
  }

  def clearSchemas(serviceName: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl(serviceName)}/setup/all-schemas"
    http.DELETE[HttpResponse](url)
  }

  def clearStub(serviceName: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl(serviceName)}/setup/all-data"
    http.DELETE[HttpResponse](url)
  }
}
