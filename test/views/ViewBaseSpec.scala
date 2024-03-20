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

package views

import mocks.MockAppConfig
import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import org.scalatest.matchers.should.Matchers
import play.twirl.api.Html
import utils.LoggerUtil

import scala.jdk.CollectionConverters._

trait ViewBaseSpec extends AnyWordSpecLike with GuiceOneAppPerSuite with BeforeAndAfterEach with Matchers with LoggerUtil{

  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)
  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit val user: User = User("999999999")
  val agentUser: User = User("999999999", arn = Some("XARN1234567"))
  lazy val injector: Injector = app.injector
  lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]
  implicit lazy val messages: MessagesImpl = MessagesImpl(Lang("en-GB"), messagesApi)

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())
  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if(elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }

  def elementExistsOnce(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    elements.size match {
      case 1 => succeed
      case 0 => fail(s"Element with selector '$cssSelector' was not found!")
      case x => fail(s"Element with selector '$cssSelector' was found $x times!")
    }
  }
  def elementExtinct(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      succeed
    } else {
      fail(s"Element with selector '$cssSelector' was found!")
    }
  }

  def elementText(selector: String)(implicit document: Document): String = {
    element(selector).text()
  }

  def elementWholeText(selector: String)(implicit document: Document): String = {
    element(selector).wholeText()
  }

  def elementAttributes(cssSelector: String)(implicit document: Document): Map[String, String] = {
    val attributes = element(cssSelector).attributes.asList().asScala.toList
    attributes.map(attribute => (attribute.getKey, attribute.getValue)).toMap
  }

  def formatHtml(markup: String): String = Jsoup.parseBodyFragment(s"\n$markup\n").toString.trim

  def pageWithExpectedMessages(checks: Seq[(String, String)])(implicit document: Document): Unit = checks.foreach {
    case (cssSelector, message) =>

      s"element with cssSelector '$cssSelector'" must {

        s"have message '$message'" in {
          val elem = document.select(cssSelector)
          elem.first.text() shouldBe message
        }
      }
  }

  def pageWithLink(selector: String, msg: String, url: String)(implicit document: Document): Unit = {
    s"has a link to $msg ($url)" which {

      "has the correct text" in {

        val actualMsg = document.select(selector).text

        logger.debug(s"[pageWithLink] \n expectedMsg: $msg \n actualMsg: $actualMsg")
        actualMsg shouldBe  msg
      }

      "has the correct destination" in {
        document.select(selector).attr("href") shouldBe url
      }
    }
  }
}
