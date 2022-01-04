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

import java.time.LocalDate

import common.SessionKeys
import config.AppConfig
import javax.inject.Inject
import models.{CustomerInformation, DDIDetails, DirectDebitStatus}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services._
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, OnlyRelative, RedirectUrl}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.interrupt.{DDInterruptExistingDD, DDInterruptNoDD}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class DDInterruptController @Inject()(paymentsService: PaymentsService,
                                      authorisedController: AuthorisedController,
                                      accountDetailsService: AccountDetailsService,
                                      dateService: DateService,
                                      mcc: MessagesControllerComponents,
                                      ddInterruptNoDDView: DDInterruptNoDD,
                                      ddInterruptExistingDDView: DDInterruptExistingDD)
                                     (implicit appConfig: AppConfig , ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil{

  def extractRedirectUrl(url: String)(implicit appConfig: AppConfig): Option[String] = {
    try {
      if (url.nonEmpty) {
        RedirectUrl(url).getEither(OnlyRelative | AbsoluteWithHostnameFromAllowlist(appConfig.environmentHost)) match {
          case Right(value) =>
            Some(value.toString())
          case Left(_) =>
            logger.warn("[DDInterruptController][extractRedirectUrl] redirectUrl was an invalid absolute url")
            None
        }
      } else {
        logger.warn("[DDInterruptController][extractRedirectUrl] couldn't create ContinueUrl from empty string.")
        None
      }
    } catch {
      case e: Exception =>
        logger.warn("[DDInterruptController][extractRedirectUrl] couldn't create ContinueUrl from what was provided.", e)
        None
    }
  }

  private[controllers] def migratedWithin4M(customerInfo: CustomerInformation): Boolean = {
    val monthLimit: Int = 4
    lazy val cutOffDate: LocalDate = dateService.now().minusMonths(monthLimit)
    customerInfo.customerMigratedToETMPDate.map(LocalDate.parse) match {
      case Some(date) => date.isAfter(cutOffDate)
      case None => false
    }
  }

  private[controllers] def dateBeforeOrWithin7Days(customerInfo: CustomerInformation,
                                                   directDebit: DirectDebitStatus): Boolean = {
    val migrationDate: Option[LocalDate] = customerInfo.customerMigratedToETMPDate.map(LocalDate.parse)
    val ddDates: Option[Seq[DDIDetails]] = directDebit.directDebitDetails
    (migrationDate, ddDates) match {
      case (Some(migDate), Some(dates)) =>
        dates.exists(ddi => LocalDate.parse(ddi.dateCreated).compareTo(migDate.plusWeeks(1)) <= 0)
      case _ => false
    }
  }

  def directDebitInterruptCall(redirectUrl: String): Action[AnyContent] = authorisedController.authorisedAction {
    implicit request => implicit user =>
     val cleanRedirectUrl = extractRedirectUrl(redirectUrl).getOrElse(controllers.routes.VatDetailsController.details().url)
       if (appConfig.features.directDebitInterrupt()) {
        accountDetailsService.getAccountDetails(user.vrn).flatMap {
          case Right(details) if migratedWithin4M(details) =>
            paymentsService.getDirectDebitStatus(user.vrn).map {
              case Right(directDebit) if !directDebit.directDebitMandateFound =>
                Ok(ddInterruptNoDDView(cleanRedirectUrl)).addingToSession(SessionKeys.viewedDDInterrupt -> "true")
              case Right(directDebit) if dateBeforeOrWithin7Days(details, directDebit) =>
                Ok(ddInterruptExistingDDView(cleanRedirectUrl)).addingToSession(SessionKeys.viewedDDInterrupt -> "true")
              case _ => Redirect(cleanRedirectUrl).addingToSession(SessionKeys.viewedDDInterrupt -> "true")
            }
          case _ => Future.successful(Redirect(cleanRedirectUrl).addingToSession(SessionKeys.viewedDDInterrupt -> "true"))
        }
      } else {
        Future.successful(Redirect(cleanRedirectUrl).addingToSession(SessionKeys.viewedDDInterrupt -> "true"))
      }
  }
}
