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

package testOnly.controllers

import config.AppConfig
import controllers.AuthorisedController
import controllers.predicates.DDInterruptPredicate
import models.viewModels.WhatYouOweChargeModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.payments.ChargeTypeDetailsView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ChargeBreakdownController @Inject()(authorisedController: AuthorisedController,
                                          DDInterrupt: DDInterruptPredicate,
                                          mcc: MessagesControllerComponents,
                                          serviceInfoService: ServiceInfoService,
                                          view: ChargeTypeDetailsView)
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  def showBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      serviceInfoService.getPartial.map { navLinks =>
        val model = WhatYouOweChargeModel(
          "Example description",
          "Example Charge",
          111.11,
          333.33,
          Some(222.22),
          LocalDate.parse("2018-03-01"),
          Some("18AA"),
          isOverdue = true,
          Some("ABCD"),
          "http://localhost:9152/vat-through-software/make-payment/11111/02/2018/2018-02-01/VAT%20FTN%20RCSL/2018-03-01/ABCD",
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-01"))
        )
        Ok(view(model, navLinks))
      }
    }
  }
}
