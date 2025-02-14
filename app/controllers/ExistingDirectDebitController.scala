/*
 * Copyright 2025 HM Revenue & Customs
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

import models.viewModels.{ExistingDDContinuePayment, ExistingDirectDebitFormModel, ExistingDirectDebitViewModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.ExistingDirectDebit
import config.AppConfig
import forms.ExistingDirectDebitFormProvider
import models.viewModels.ExistingDDContinuePayment.Yes
import models.viewModels.helpers.Enumerable
import play.api.data.Form
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.idFunctor
import uk.gov.hmrc.play.bootstrap.binders.{OnlyRelative, RedirectUrl}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExistingDirectDebitController @Inject()(authorisedController: AuthorisedController,
                                              mcc: MessagesControllerComponents,
                                              serviceInfoService: ServiceInfoService,
                                              view: ExistingDirectDebit,
                                              formProvider: ExistingDirectDebitFormProvider
                                             )(implicit ec: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil with Enumerable.Implicits {

  val form: Form[ExistingDirectDebitFormModel] = formProvider()

  def show(dueDateOrUrl: String, linkId: String, ddStatus: Boolean): Action[AnyContent] = authorisedController.financialAction {
    implicit request =>
      implicit user =>
        serviceInfoService.getPartial map {
          serviceInfoContent =>
            Ok(view(ExistingDirectDebitViewModel(Some(dueDateOrUrl), linkId, ddStatus, None),
              form, ExistingDDContinuePayment.options, serviceInfoContent))
          }
      }

  def showFromChargeBreakdown(redirectUrl: RedirectUrl, linkId: String, ddStatus: Boolean): Action[AnyContent] = authorisedController.financialAction {
    implicit request =>
      implicit user =>
        serviceInfoService.getPartial map {
          serviceInfoContent =>
            Ok(view(ExistingDirectDebitViewModel(None,linkId, ddStatus, Some(redirectUrl)),
              form, ExistingDDContinuePayment.options, serviceInfoContent))
        }
  }

  def submit() : Action[AnyContent] = authorisedController.financialAction {
    implicit request => {
      implicit user => {
        serviceInfoService.getPartial map {
          serviceInfoContent =>
            form.bindFromRequest()
              .fold(
                formWithErrors => BadRequest(view(
                  ExistingDirectDebitViewModel(formWithErrors.data.get("dueDateOrUrl"),
                    formWithErrors.data.get("linkId").get,
                    formWithErrors.data.get("directDebitMandateFound").get.toBoolean,
                    Some(RedirectUrl(formWithErrors.data.get("redirectUrl").get))
                    ),
                   formWithErrors, ExistingDDContinuePayment.options, serviceInfoContent)),
                formModel => {
                  formModel.value match {
                    case Yes =>
                      formModel.linkId match {
                        case "wyo" =>
                          val makePaymentRedirect: String = controllers.routes.MakePaymentController.makeGenericPayment(
                            earliestDueDate = formModel.dueDateOrUrl,
                            linkId = "existing-dd-pay-now-button"
                          ).url
                          infoLog(s"[ExistingDirectDebitController] [submit] " +
                            s"User clicked Yes to pay even DD has hence navigating to payment " + makePaymentRedirect)
                           Redirect(makePaymentRedirect)
                        case _ =>
                          infoLog(s" [ExistingDirectDebitController] [submit] " +
                            s"User clicked Yes to pay even DD has hence navigating to payment " + formModel.dueDateOrUrl.get)
                          println("formmodel>>>>>> " + formModel)

                          Redirect(formModel.redirectUrl.get.get(OnlyRelative).url)
                      }
                    case _ =>
                      val wyoLink: String = controllers.routes.WhatYouOweController.show.url
                      infoLog(s"[ExistingDirectDebitController] [submit] " +
                        s"User clicked No to pay hence navigating to wyo " + wyoLink)
                      Redirect(wyoLink)
                  }
                }
              )
        }
      }
    }
  }
}
