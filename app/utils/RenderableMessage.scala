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

package utils

import play.twirl.api.Html

case class MoneyPounds(value: BigDecimal, decimalPlaces: Int = 2, roundUp: Boolean = false) {

  def isNegative: Boolean = value < 0

  def quantity: String =
    s"%,.${decimalPlaces}f".format(
      value
        .setScale(decimalPlaces, if (roundUp) BigDecimal.RoundingMode.CEILING else BigDecimal.RoundingMode.FLOOR)
        .abs
    )
}

case class RenderableMoneyMessage(moneyPounds: MoneyPounds) extends {

  private val maybeMinus = if (moneyPounds.isNegative) "&minus;" else ""

  val render: Html = Html(s"$maybeMinus&pound;${moneyPounds.quantity}")

}

object Money {
  def pounds(value: BigDecimal, decimalPlaces: Int = 0): Html =
    RenderableMoneyMessage(MoneyPounds(value, decimalPlaces)).render
}