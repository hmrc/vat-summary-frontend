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

import javax.inject.{Inject, Singleton}

import config.{AppConfig, VatHeaderCarrierForPartialsConverter}
import play.api.Logger
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial._
import uk.gov.hmrc.play.partials.HtmlPartial

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BtaHeaderPartialConnector @Inject()(http: HttpClient, appConfig: AppConfig,
                                          partialConverter: VatHeaderCarrierForPartialsConverter) {

  import partialConverter._

  lazy val btaUrl: String = s"${appConfig.btaService}/business-account/partial/service-info"

  def getBtaHeaderPartial()(implicit request: Request[AnyContent]): Future[Html] = {
    http.GET[HtmlPartial](btaUrl) recover connectionExceptionsAsHtmlPartialFailure map { partial =>
      partial.successfulContentOrEmpty
    } recover {
      case _ =>
        Logger.warn("BtaHeaderPartialConnector - Unexpected future failed error")
        Html("")
    }
  }
}

