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

package views.templates

import mocks.MockAppConfig
import models.User
import org.jsoup.Jsoup
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.twirl.api.Html
import org.scalatest.matchers.should.Matchers


class TemplateBaseSpec extends AnyWordSpecLike with MockFactory with GuiceOneAppPerSuite with Matchers {

  val injector: Injector = app.injector
  val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit val mockAppConfig: MockAppConfig = new MockAppConfig(app.configuration)
  lazy implicit val messages: MessagesImpl = MessagesImpl(Lang("en-GB"), messagesApi)
  lazy implicit val lang: Lang = injector.instanceOf[Lang]
  implicit val user: User = User("999999999")

  def formatHtml(body: Html): String = Jsoup.parseBodyFragment(s"\n$body\n").toString.trim
}
