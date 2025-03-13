/*
 * Copyright 2023 HM Revenue & Customs
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

package helpers

import common.SessionKeys
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Injecting
import play.api.{Application, Environment, Mode}
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind

trait IntegrationBaseSpec extends AnyWordSpecLike with Matchers with ScalaFutures with WireMockHelper with
  GuiceOneServerPerSuite with BeforeAndAfterEach with BeforeAndAfterAll with Injecting {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wireMockPort.toString
  val appRouteContext: String = "/vat-through-software"

  def formatSessionVrn: Option[String] => Map[String, String] =
    _.fold(Map.empty[String, String])(x => Map(SessionKeys.mtdVatvcClientVrn -> x))

 override lazy val client: WSClient = app.injector.instanceOf[WSClient]
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  def servicesConfig: Map[String, String] = Map(
    "play.http.router" -> "testOnly.Routes",
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.financial-transactions.host" -> mockHost,
    "microservice.services.financial-transactions.port" -> mockPort,
    "microservice.services.pay-api.host" -> mockHost,
    "microservice.services.pay-api.port" -> mockPort,
    "microservice.services.vat-sign-up-frontend.host" -> mockHost,
    "microservice.services.vat-sign-up-frontend.port" -> mockPort,
    "microservice.services.vat-subscription.host" -> mockHost,
    "microservice.services.vat-subscription.port" -> mockPort,
    "microservice.services.vat-obligations.host" -> mockHost,
    "microservice.services.vat-obligations.port" -> mockPort,
    "microservice.services.direct-debit.host" -> mockHost,
    "microservice.services.direct-debit.port" -> mockPort,
    "microservice.services.business-tax-account.host" -> mockHost,
    "microservice.services.business-tax-account.port" -> mockPort,
    "microservice.services.essttp-backend.host" -> mockHost,
    "microservice.services.essttp-backend.port" -> mockPort,
    "penalties.host" -> s"http://$mockHost:$mockPort"
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def buildRequest(path: String,
                   additionalCookies: Map[String, String] = Map.empty,
                   lang: Option[String] = Some("en")): WSRequest = {
    val authSession = Map("authToken"-> "mock-bearer-token")
    client.url(s"http://localhost:$port$appRouteContext$path")
      .withHttpHeaders(
        HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(additionalCookies ++ authSession, lang),
        "Csrf-Token" -> "nocheck"
      )
      .withFollowRedirects(false)
  }

  def document(response: WSResponse): Document = Jsoup.parse(response.body)
}
