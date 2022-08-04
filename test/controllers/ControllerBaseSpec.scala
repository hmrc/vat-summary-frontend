/*
 * Copyright 2022 HM Revenue & Customs
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
import audit.models.{AuditModel, ExtendedAuditModel}
import common.SessionKeys
import common.TestModels.{agentAuthResult, agentEnrolments, authResultWithVatDec, penaltyDetailsResponse, successfulAuthResult}
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.{AgentPredicate, DDInterruptPredicate, FinancialPredicate}
import mocks.MockAppConfig
import models.{CustomerInformation, ServiceResponse, User, WYODatabaseModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, MessagesControllerComponents}
import play.api.test.FakeRequest
import services._
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, Enrolments, InsufficientEnrolments, MissingBearerToken}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys => GovUKSessionKeys}
import org.scalatest.wordspec.AnyWordSpecLike
import views.html.errors.{AgentUnauthorised, Unauthorised, UserInsolventError}
import java.time.LocalDate
import models.payments.Payments
import models.viewModels.ChargeDetailsViewModel
import org.scalatest.enablers.Existence
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

  val baseSession = Seq(SessionKeys.insolventWithoutAccessKey -> "false", SessionKeys.viewedDDInterrupt -> "true")
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(baseSession: _*)
  implicit lazy val fakePostRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "").withSession(baseSession: _*)

  val mockPaymentsService: PaymentsService = mock[PaymentsService]
  val agentUnauthorised: AgentUnauthorised = injector.instanceOf[AgentUnauthorised]
  val unauthorised: Unauthorised = injector.instanceOf[Unauthorised]
  val userInsolvent: UserInsolventError = injector.instanceOf[UserInsolventError]
  val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
  val mockDateService: DateService = mock[DateService]
  val mockServiceInfoService: ServiceInfoService = mock[ServiceInfoService]
  val mockAuditService: AuditingService = mock[AuditingService]
  val mockPenaltyDetailsService: PenaltyDetailsService = mock[PenaltyDetailsService]
  val mockWYOSessionService: WYOSessionService = mock[WYOSessionService]
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
    unauthorised,
    userInsolvent
  )
  val ddInterruptPredicate: DDInterruptPredicate = new DDInterruptPredicate(mcc)

  val requestSession = Seq(
    GovUKSessionKeys.lastRequestTimestamp -> "1498236506662",
    GovUKSessionKeys.authToken -> "Bearer Token",
    SessionKeys.migrationToETMP -> "2018-01-01",
    SessionKeys.financialAccess -> "true"
  )

  lazy val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession(requestSession: _*)

  lazy val fakePostWithSession = fakePostRequest.withSession(requestSession: _*)

  lazy val insolventRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(SessionKeys.insolventWithoutAccessKey -> "true")

  lazy val DDInterruptRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET","/homepage")
      .withSession(SessionKeys.insolventWithoutAccessKey -> "false", SessionKeys.financialAccess -> "true")

  val agentSession = Seq(
    SessionKeys.financialAccess -> "true",
    SessionKeys.mtdVatvcClientVrn -> "123456789",
    SessionKeys.viewedDDInterrupt -> "false"
  )

  lazy val agentFinancialRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(agentSession: _*)

  lazy val agentPostFinancialRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", "").withSession(agentSession: _*)

  def fakeRequestToPOSTWithSession(input: (String, String)*): FakeRequest[AnyContentAsFormUrlEncoded] =
    fakeRequestWithSession.withFormUrlEncodedBody(input: _*)

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.directDebitInterrupt(true)
  }

  def mockOpenPayments(result: ServiceResponse[Option[Payments]]): Any =
    (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(result))

  def mockCustomerInfo(accountDetailsResponse: HttpGetResult[CustomerInformation]): Any =
    (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(accountDetailsResponse))

  def mockDateServiceCall(): Any =
    (mockDateService.now: () => LocalDate)
    .stubs()
    .returns(LocalDate.parse("2018-05-01"))

  def mockServiceInfoCall(): Any =
    (mockServiceInfoService.getPartial(_: User, _: HeaderCarrier, _: ExecutionContext, _: Messages))
      .stubs(*, *, *, *)
      .returns(Future.successful(Html("")))

  def mockWYOSessionServiceCall(model: Option[WYODatabaseModel]): Any =
    (mockWYOSessionService.retrieveViewModel(_: String))
      .stubs(*)
      .returns(Future.successful(model))

  def mockPenaltyDetailsServiceCall(): Any =
    (mockPenaltyDetailsService.getPenaltyDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(penaltyDetailsResponse))

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
  def mockVatDec(): Any = mockAuth(isAgent = false, authResultWithVatDec)

  def mockAudit(): Any = {
    (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *)
      .returns({})
    (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *)
      .returns({})
  }

  def mockWYOSessionServiceCall(): Any = {
    (mockWYOSessionService.storeChargeModels(_: Seq[ChargeDetailsViewModel],_ : String)(_: ExecutionContext))
      .stubs(*,*,*)
      .returns(Future.successful(Seq()))
  }

  implicit def existenceOfElement[Els <: org.jsoup.select.Elements]: Existence[Els] =
    (els: Els) => els.size > 0
}
