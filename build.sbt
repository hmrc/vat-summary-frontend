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

import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings.*

val appName: String = "vat-summary-frontend"
lazy val appDependencies: Seq[ModuleID] = compile ++ test()

scalacOptions ++= Seq("-Wconf:cat=unused-imports&site=.*views.html.*:s")

lazy val coverageSettings: Seq[Setting[_]] = {
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

val mongoVersion = "1.2.0"

val compile: Seq[ModuleID] = Seq(
  ws,
  "uk.gov.hmrc"       %% "bootstrap-frontend-play-28" % "7.15.0",
  "uk.gov.hmrc"       %% "play-frontend-hmrc"         % "7.7.0-play-28",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"         % mongoVersion,
)

def test(scope: String = "test, it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"        %% "bootstrap-test-play-28"      % "7.15.0"      % scope,
  "org.scalatestplus"  %% "mockito-3-4"                 % "3.2.9.0"     % scope,
  "org.scalamock"      %% "scalamock"                   % "5.2.0"       % scope,
  "uk.gov.hmrc.mongo"  %% "hmrc-mongo-test-play-28"     % mongoVersion  % scope
)

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._"
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = tests map {
  test =>
    Group(
      test.name,
      Seq(test),
      SubProcess(ForkOptions().withRunJVMOptions(Vector("-Dtest.name=" + test.name, "-Dlogger.resource=logback-test.xml")))
    )
}

lazy val microservice: Project = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(PlayKeys.playDefaultPort := 9152)
  .settings(majorVersion := 0)
  .settings(coverageSettings: _*)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    Test / Keys.fork := true,
    Test / javaOptions += "-Dlogger.resource=logback-test.xml",
    scalaVersion := "2.13.8",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    routesGenerator := InjectedRoutesGenerator,
    routesImport := Seq.empty
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork  := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    IntegrationTest / testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    IntegrationTest / parallelExecution  := false,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
