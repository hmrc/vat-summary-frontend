/*
 * Copyright 2017 HM Revenue & Customs
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

package connectors

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial.HtmlPartialHttpReads

import scala.concurrent.Future

class PartialsConnector @Inject()(wSHttp: WSHttp) extends HtmlPartialHttpReads {

  def getPartial(url: String)(implicit hc: HeaderCarrier): Future[HtmlPartial] = {
    wSHttp.GET[HttpResponse](url).map { response =>
      read("GET", url, response)
    }
  }
}
