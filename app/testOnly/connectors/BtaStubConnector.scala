/*
 * Copyright 2020 HM Revenue & Customs
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

import config.VatHeaderCarrierForPartialsConverter
import javax.inject.Inject
import play.api.http.Status._
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.http.{HttpResponse, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial.HtmlPartialHttpReads

import scala.concurrent.{ExecutionContext, Future}

class BtaStubConnector @Inject()(http: HttpClient,
                                 hc: VatHeaderCarrierForPartialsConverter,
                                 implicit val ec: ExecutionContext)
  extends HtmlPartialHttpReads {

  import hc._

  def getPartial(partialUrl: String)(implicit request: Request[AnyContent]): Future[HtmlPartial] = {
    val result: Future[HttpResponse] = http.GET[HttpResponse](partialUrl)
      .recover {
        case ex: Upstream4xxResponse
          if ex.upstreamResponseCode == UNAUTHORIZED || ex.upstreamResponseCode == FORBIDDEN =>
          HttpResponse(ex.upstreamResponseCode, responseString = Some(ex.message))
      }
    result.map(p => read("GET", partialUrl, p))
  }
}
