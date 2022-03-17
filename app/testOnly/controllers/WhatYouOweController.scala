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

import com.google.inject.Inject
import config.AppConfig
import controllers.AuthorisedController
import controllers.predicates.DDInterruptPredicate
import models.viewModels.{WhatYouOweChargeModel, WhatYouOweViewModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.payments.WhatYouOwe
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class WhatYouOweController @Inject()(serviceInfoService: ServiceInfoService,
                                     authorisedController: AuthorisedController,
                                     ddInterrupt: DDInterruptPredicate,
                                     mcc: MessagesControllerComponents,
                                     whatYouOwe: WhatYouOwe)
                                    (implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  implicit val ec: ExecutionContext = mcc.executionContext

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => ddInterrupt.interruptCheck { _ =>

      serviceInfoService.getPartial.flatMap { serviceInfoContent =>

        val viewModel = WhatYouOweViewModel(
          totalAmount = 1000.00,
          charges = Seq(WhatYouOweChargeModel(
            chargeDescription = "VAT OA Debit Charge",
            chargeTitle = "VAT OA Debit Charge",
            outstandingAmount = 1000.00,
            originalAmount = 1000.00,
            clearedAmount = Some(00.00),
            dueDate = LocalDate.parse("2017-03-08"),
            periodKey = Some("#001"),
            isOverdue = false,
            chargeReference = Some("XD002750002155"),
            makePaymentRedirect = "/vat-through-software/make-payment",
            periodFrom = Some(LocalDate.parse("2017-01-01")),
            periodTo = Some(LocalDate.parse("2017-03-01"))
          ))
        )

        Future.successful(Ok(whatYouOwe(viewModel, serviceInfoContent)))
      }
    }
  }
}
