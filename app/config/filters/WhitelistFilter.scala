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

package config.filters

import javax.inject.{Inject, Singleton}

import config.AppConfig
import play.api.Application
import play.api.mvc.Call
import uk.gov.hmrc.play.config.RunMode
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.whitelist.AkamaiWhitelistFilter

@Singleton
class WhitelistFilter @Inject()(app: Application) extends AkamaiWhitelistFilter with RunMode with MicroserviceFilterSupport {

  private lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  override lazy val whitelist: Seq[String] = appConfig.whitelistedIps

  override lazy val destination: Call = Call("GET", appConfig.shutterPage)

  override lazy val excludedPaths: Seq[Call] = appConfig.whitelistExcludedPaths
}

