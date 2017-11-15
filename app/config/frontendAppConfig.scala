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

package config

import java.util.Base64
import javax.inject.{Inject, Singleton}

import play.api.Mode.Mode
import play.api.mvc.Call
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val whitelistEnabled: Boolean
  val whitelistedIps: Seq[String]
  val whitelistExcludedPaths: Seq[Call]
  val shutterPage: String
  val authUrl: String
  val signInUrl: String
  val signInContinueBaseUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, val environment: Environment) extends AppConfig {

  override val mode: Mode = environment.mode

  private def loadConfig(key: String): String = runModeConfiguration.getString(key)
    .getOrElse(throw new Exception(s"Missing runModeConfiguration key: $key"))

  private lazy val contactHost: String = runModeConfiguration.getString(s"contact-frontend.host").getOrElse("")
  private lazy val contactFormServiceIdentifier: String = "MyService"

  override lazy val authUrl: String = baseUrl("auth")
  override lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  override lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private def whitelistConfig(key: String): Seq[String] = Some(new String(Base64.getDecoder
    .decode(runModeConfiguration.getString(key).getOrElse("")), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = runModeConfiguration.getBoolean("whitelist.enabled").getOrElse(true)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig("whitelist.allowedIps")
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig("whitelist.excludePaths").map(path => Call("GET", path))
  override lazy val shutterPage: String = loadConfig("whitelist.shutter-page-url")

  private lazy val signInBaseUrl: String = loadConfig("signIn.url")
  override lazy val signInContinueBaseUrl: String = runModeConfiguration.getString("signIn.continueBaseUrl").getOrElse("")
  private lazy val signInContinueUrl: String = ContinueUrl(signInContinueBaseUrl + controllers.routes.HelloWorldController.helloWorld().url).encodedUrl
  private lazy val signInOrigin = loadConfig("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"
}
