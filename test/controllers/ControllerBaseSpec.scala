/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.{AgentPredicate, DDInterruptPredicate, FinancialPredicate}
import mocks.MockAppConfig
import models.CustomerInformation
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, MessagesControllerComponents}
import play.api.test.FakeRequest
import services.{AccountDetailsService, DateService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys => GovUKSessionKeys}
import uk.gov.hmrc.play.test.UnitSpec
import views.html.errors.{AgentUnauthorised, Unauthorised}
import java.time.LocalDate

import scala.concurrent.{ExecutionContext, Future}

class ControllerBaseSpec extends UnitSpec with MockFactory with GuiceOneAppPerSuite with BeforeAndAfterEach {

  lazy val injector: Injector = app.injector
  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = messagesApi.preferred(Seq(Lang("en")))
  lazy val mockServiceErrorHandler: ServiceErrorHandler = injector.instanceOf[ServiceErrorHandler]
  val mcc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "false",
    SessionKeys.viewedDDInterrupt -> "true")

  val agentUnauthorised: AgentUnauthorised = injector.instanceOf[AgentUnauthorised]
  val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]
  val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
  val mockDateService: DateService = mock[DateService]
  val financialPredicate: FinancialPredicate = new FinancialPredicate(
    mockAccountDetailsService, mockServiceErrorHandler, mcc, mockDateService)
  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val enrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
  val agentPredicate: AgentPredicate = new AgentPredicate(
    enrolmentsAuthService, mcc, mockAppConfig, ec, agentUnauthorised)
  val authorisedController: AuthorisedController = new AuthorisedController(
    mcc,
    enrolmentsAuthService,
    financialPredicate,
    agentPredicate,
    mockAccountDetailsService,
    mockServiceErrorHandler,
    mockAppConfig,
    ec,
    unauthorised
  )
  val ddInterruptPredicate: DDInterruptPredicate = new DDInterruptPredicate(
    mcc
  )

  lazy val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(
    GovUKSessionKeys.lastRequestTimestamp -> "1498236506662",
    GovUKSessionKeys.authToken -> "Bearer Token",
    SessionKeys.migrationToETMP -> "2018-01-01",
    SessionKeys.financialAccess -> "true"
  )

  lazy val insolventRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "true")

  def fakeRequestToPOSTWithSession(input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequestWithSession.withFormUrlEncodedBody(input: _*)


  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.agentAccess(true)
    mockAppConfig.features.ddCollectionInProgressEnabled(true)
    mockAppConfig.features.directDebitInterrupt(true)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    mockAppConfig.features.agentAccess(true)
    mockAppConfig.features.ddCollectionInProgressEnabled(true)
  }

  def mockCustomerInfo(accountDetailsResponse: Future[HttpGetResult[CustomerInformation]]):Any =
    (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(accountDetailsResponse)

  def mockDateServiceCall(): Any =
    (mockDateService.now: () => LocalDate)
    .stubs()
    .returns(LocalDate.parse("2018-05-01"))
}
