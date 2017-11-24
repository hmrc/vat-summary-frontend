/*
 * Copyright 2017 HM Revenue & Customs
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

package models

sealed trait HttpError {
  def message: String
}

// === Obligation data errors ===
case object InvalidVrnError extends HttpError {
  override val message: String = "An invalid VRN was sent to the VAT API."
}
case object InvalidFromDateError extends HttpError {
  override val message: String = "An invalid 'from' date was sent to the VAT API."
}
case object InvalidToDateError extends HttpError {
  override val message: String = "An invalid 'to' date was sent to the VAT API."
}
case object InvalidDateRangeError extends HttpError {
  override val message: String = "An invalid date range was sent to the VAT API."
}
case object InvalidStatusError extends HttpError {
  override val message: String = "An invalid obligation status was sent to the VAT API."
}
// ==============================


case object ServerSideError extends HttpError {
  override val message: String = "The server you connecting to returned an error."
}

case class UnexpectedStatusError(status: Int) extends HttpError {
  override val message: String = s"Received an unexpected status code: $status."
}

case object MultipleErrors extends HttpError {
  override val message: String = "Received multiple errors."
}

case object UnknownError extends HttpError {
  override val message: String = "Received an unknown error."
}
