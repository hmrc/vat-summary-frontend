/*
 * Copyright 2023 HM Revenue & Customs
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

import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.*

val appName: String = "vat-summary-frontend"
lazy val appDependencies: Seq[ModuleID] = compile ++ test()
RoutesKeys.routesImport := Seq("uk.gov.hmrc.play.bootstrap.binders.RedirectUrl")

scalacOptions ++= Seq("-Wconf:cat=unused-imports&site=.*views.html.*:s")

lazy val coverageSettings: Seq[Setting[?]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "views.*",
    ".*Reverse.*",
    ".*Routes.*",
    "config.*",
    "testOnly.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

val mongoVersion = "2.6.0"
val bootstrapPlayVersion = "8.6.0"

val compile: Seq[ModuleID] = Seq(
  ws,
  "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
  "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % "9.11.0",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoVersion,
)

def test(scope: String = "test, it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"        %% "bootstrap-test-play-30"      % bootstrapPlayVersion  % scope,
  "org.scalatestplus"  %% "mockito-4-11"                % "3.2.18.0"            % scope,
  "org.scalamock"      %% "scalamock"                   % "7.3.2"               % scope,
  "uk.gov.hmrc.mongo"  %% "hmrc-mongo-test-play-30"     % mongoVersion          % scope
)

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._"
)

lazy val microservice: Project = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(PlayKeys.playDefaultPort := 9152)
  .settings(majorVersion := 0)
  .settings(coverageSettings *)
  .settings(scalaSettings *)
  .settings(defaultSettings() *)
  .settings(
    Test / Keys.fork := true,
    Test / javaOptions += "-Dlogger.resource=logback-test.xml",
    scalaVersion := "2.13.16",
    libraryDependencies ++= appDependencies,
    scalacOptions ++= Seq("-Wconf:cat=unused-imports&src=.*routes.*:s", "-Wconf:cat=unused-imports&src=html/.*:s", "-Wconf:src=routes/.*:s"),
    retrieveManaged := true,
    routesGenerator := InjectedRoutesGenerator,
    routesImport := Seq.empty
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings) *)
  .settings(
    IntegrationTest / Keys.fork  := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    IntegrationTest / parallelExecution  := false,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
