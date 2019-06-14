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

package connectors

import config.VatHeaderCarrierForPartialsConverter
import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.partials.HtmlPartial
import uk.gov.hmrc.play.partials.HtmlPartial.{Failure, Success}

import scala.concurrent.{ExecutionContext, Future}

class ServiceInfoPartialConnectorSpec extends ControllerBaseSpec {
  val header: VatHeaderCarrierForPartialsConverter = injector.instanceOf[VatHeaderCarrierForPartialsConverter]
  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit val messagesImpl: Messages = Messages(Lang("en-GB"), messages)
  val validHtml = Html("<nav>BTA lINK</nav>")

  private trait Test {
    val result :Future[HtmlPartial] = Future.successful(Success(None,validHtml))
    val httpClient: HttpClient = mock[HttpClient]
    lazy val connector: ServiceInfoPartialConnector = {

      (httpClient.GET[HtmlPartial](_: String)(_: HttpReads[HtmlPartial],_: HeaderCarrier,_: ExecutionContext))
        .stubs(*,*,*,*)
        .returns(result)
      new ServiceInfoPartialConnector(httpClient, header)(messages, mockAppConfig)
    }

  }

  "ServiceInfoPartialConnector" should {
    "generate the correct url" in new Test {
      connector.btaUrl shouldBe "/business-account/partial/service-info"
    }
  }

  "getServiceInfoPartial" when{
    "a connectionExceptionsAsHtmlPartialFailure error is returned" should {
      "return the fall back partial" in new Test{
        override val result: Future[Failure] = Future.successful(Failure(Some(Status.GATEWAY_TIMEOUT)))
        await(connector.getServiceInfoPartial()) shouldBe views.html.templates.btaNavigationLinks()
      }
    }

    "an unexpected Exception is returned" should {
      "return the fall back partial" in new Test{
        override val result: Future[Failure] = Future.successful(Failure(Some(Status.INTERNAL_SERVER_ERROR)))
        await(connector.getServiceInfoPartial()) shouldBe views.html.templates.btaNavigationLinks()
      }
    }

    "a successful response is returned" should {
      "return the Bta partial" in new Test{
        await(connector.getServiceInfoPartial()) shouldBe validHtml
      }
    }
  }
}