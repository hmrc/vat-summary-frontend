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

import sbt._
import play.sbt.PlayImport._
import play.core.PlayVersion

object FrontendBuild extends Build with MicroService {

  val appName: String = "vat-summary-frontend"

  override lazy val appDependencies: Seq[ModuleID] = compile ++ test()

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "frontend-bootstrap" % "8.6.0",
    "uk.gov.hmrc" %% "play-partials" % "6.0.0",
    "uk.gov.hmrc" %% "play-whitelist-filter" % "2.0.0"
  )

  def test(scope: String = "test"): Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "2.4.0" % scope,
    "org.scalatest" %% "scalatest" % "3.0.1" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "org.jsoup" % "jsoup" % "1.10.3" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
    "org.mockito" % "mockito-core" % "2.9.0" % scope
  )
}
