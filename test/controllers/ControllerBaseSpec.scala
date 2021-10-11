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

import audit.AuditingService
import audit.models.ExtendedAuditModel
import common.SessionKeys
import common.TestModels.{agentAuthResult, agentEnrolments, successfulAuthResult}
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.{AgentPredicate, DDInterruptPredicate, FinancialPredicate}
import mocks.MockAppConfig
import models.{CustomerInformation, User}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, Enrolments, InsufficientEnrolments, MissingBearerToken}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys => GovUKSessionKeys}
import org.scalatest.wordspec.AnyWordSpecLike
import views.html.errors.{AgentUnauthorised, Unauthorised}

import java.time.LocalDate
import org.scalatest.matchers.should.Matchers
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}

import scala.concurrent.{ExecutionContext, Future}

class ControllerBaseSpec extends AnyWordSpecLike with MockFactory with GuiceOneAppPerSuite with BeforeAndAfterEach with Matchers {

  lazy val injector: Injector = app.injector
  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val messages: Messages = messagesApi.preferred(Seq(Lang("en")))
  lazy val mockServiceErrorHandler: ServiceErrorHandler = injector.instanceOf[ServiceErrorHandler]
  val mcc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  implicit val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "false", SessionKeys.viewedDDInterrupt -> "true")

  val agentUnauthorised: AgentUnauthorised = injector.instanceOf[AgentUnauthorised]
  val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]
  val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
  val mockDateService: DateService = mock[DateService]
  val mockServiceInfoService: ServiceInfoService = mock[ServiceInfoService]
  val mockPaymentsService: PaymentsService = mock[PaymentsService]
  val mockAuditService: AuditingService = mock[AuditingService]
  val financialPredicate: FinancialPredicate = new FinancialPredicate(
    mockAccountDetailsService, mockServiceErrorHandler, mcc, mockDateService)
  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val enrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
  val agentPredicate: AgentPredicate = new AgentPredicate(enrolmentsAuthService, mcc, agentUnauthorised, financialPredicate)
  val authorisedController: AuthorisedController = new AuthorisedController(
    mcc,
    enrolmentsAuthService,
    financialPredicate,
    agentPredicate,
    mockAccountDetailsService,
    mockServiceErrorHandler,
    unauthorised
  )
  val ddInterruptPredicate: DDInterruptPredicate = new DDInterruptPredicate(mcc)

  lazy val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(
    GovUKSessionKeys.lastRequestTimestamp -> "1498236506662",
    GovUKSessionKeys.authToken -> "Bearer Token",
    SessionKeys.migrationToETMP -> "2018-01-01",
    SessionKeys.financialAccess -> "true"
  )

  lazy val insolventRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "true")

  lazy val DDInterruptRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET","/homepage")
      .withSession(SessionKeys.insolventWithoutAccessKey -> "false", SessionKeys.financialAccess -> "true")

  lazy val agentFinancialRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(
      SessionKeys.financialAccess -> "true",
      SessionKeys.agentSessionVrn -> "123456789",
      SessionKeys.viewedDDInterrupt -> "false"
    )

  def fakeRequestToPOSTWithSession(input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequestWithSession.withFormUrlEncodedBody(input: _*)

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.directDebitInterrupt(true)
  }

  def mockCustomerInfo(accountDetailsResponse: HttpGetResult[CustomerInformation]): Any =
    (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(accountDetailsResponse))

  def mockDateServiceCall(): Any =
    (mockDateService.now: () => LocalDate)
    .stubs()
    .returns(LocalDate.parse("2018-05-01"))

  def mockServiceInfoCall(): Any =
    (mockServiceInfoService.getPartial(_: Request[_], _: User, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(Html("")))

  def mockAuth(isAgent: Boolean, authResult: Future[~[Enrolments, Option[AffinityGroup]]]): Any = {
    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])
                                (_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResult)

    if(isAgent) {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(agentEnrolments))
        .noMoreThanOnce()
    }
  }

  def mockPrincipalAuth(): Any = mockAuth(isAgent = false, successfulAuthResult)
  def mockAgentAuth(): Any = mockAuth(isAgent = true, agentAuthResult)
  def mockInsufficientEnrolments(): Any = mockAuth(isAgent = false, Future.failed(InsufficientEnrolments()))
  def mockMissingBearerToken(): Any = mockAuth(isAgent = false, Future.failed(MissingBearerToken()))

  def mockAudit(): Any =
    (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *)
      .returns({})
}
