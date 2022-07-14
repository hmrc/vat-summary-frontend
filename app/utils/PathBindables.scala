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

package utils

import models.viewModels.StandardChargeViewModel
import play.api.mvc.PathBindable

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

object PathBindables extends LoggerUtil {

  val none = "None"

  implicit val standardChargePathBinder: PathBindable[StandardChargeViewModel] = new PathBindable[StandardChargeViewModel] {
    override def bind(key: String, value: String): Either[String, StandardChargeViewModel] =
      Try {
        val params: Array[String] = value.split('+')
        val chargeType = params(0)
        val outstandingAmount = BigDecimal(params(1))
        val originalAmount = BigDecimal(params(2))
        val clearedAmount = BigDecimal(params(3))
        val dueDate = LocalDate.parse(params(4))
        val periodKey = if(params(5) == none) None else Some(params(5))
        val isOverdue = params(6).toBoolean
        val chargeReference = if(params(7) == none) None else Some(params(7))
        val periodFrom = if(params(8) == none) None else Some(LocalDate.parse(params(8)))
        val periodTo = if(params(9) == none) None else Some(LocalDate.parse(params(9)))
        StandardChargeViewModel(
          chargeType, outstandingAmount, originalAmount, clearedAmount, dueDate, periodKey,
          isOverdue, chargeReference, periodFrom, periodTo
        )
      } match {
        case Success(model) => Right(model)
        case Failure(error) =>
          logger.warn(s"[PathBindables][standardChargePathBinder] - Failed to bind due to error: $error")
          Left(s"Failed to bind due to error: $error")
      }

    override def unbind(key: String, value: StandardChargeViewModel): String = value.toString
  }


}
