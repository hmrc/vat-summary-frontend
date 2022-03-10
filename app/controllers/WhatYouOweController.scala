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

import com.google.inject.Inject
import models.payments.WhatYouOweChargeModel
import models.viewModels.WhatYouOweViewModel
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

class WhatYouOweController @Inject()(authorisedController: AuthorisedController) {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user =>

      val viewModel = WhatYouOweViewModel(
        totalAmount = 1000.00,
        charges = Seq(WhatYouOweChargeModel(
          chargeDescription = "VAT OA Debit Charge",
          chargeTitle = "VAT OA Debit Charge",
          outstandingAmount = 1000.00,
          originalAmount = 1000.00,
          clearedAmount = 00.00,
          dueDate = LocalDate.parse("2017-03-08"),
          periodKey = Some("#001"),
          isOverdue = false,
          chargeReference = Some("XD002750002155"),
          makePaymentRedirect = "/vat-through-software/make-payment",
          periodFrom = Some(LocalDate.parse("2017-01-01")),
          periodTo = Some(LocalDate.parse("2017-03-01"))
        ))
      )

      Future.successful(Ok("view"))
  }
}
