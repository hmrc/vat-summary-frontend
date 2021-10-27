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

package services

import connectors.ServiceInfoPartialConnector

import javax.inject.{Inject, Singleton}
import models.{ListLinks, NavContent, User}
import play.api.http.HeaderNames
import play.api.i18n.{Lang, Messages}
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.templates.NavLinksView

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ServiceInfoService @Inject()(serviceInfoPartialConnector: ServiceInfoPartialConnector, btaLinks: NavLinksView) {

  def getPartial(implicit user: User, hc: HeaderCarrier, ec: ExecutionContext, messages: Messages): Future[Html] =
    if(user.isAgent){
      Future.successful(HtmlFormat.empty)
    } else {
      val hcWithCookie = hc.copy(extraHeaders = hc.headers(Seq(HeaderNames.COOKIE)))
      serviceInfoPartialConnector.getNavLinks()(hcWithCookie, ec).map { links =>
        val listLinks = partialList(links)
        btaLinks(listLinks)
      }
    }

  def notificationBadgeCount(messageCount: Int): String =
    messageCount match {
      case 0 => "0"
      case count if count > 99  => "+99"
      case _ => s"$messageCount"
    }

  def partialList(navLinks: Option[NavContent])(implicit messages: Messages): Seq[ListLinks] =
    navLinks match {
      case Some(NavContent(home, account, message, help)) =>
        if(messages.lang == Lang("cy")){
          Seq(
            ListLinks(home.cy, home.url),
            ListLinks(account.cy, account.url),
            ListLinks(message.cy, message.url, Some(notificationBadgeCount(message.alerts.getOrElse(0)))),
            ListLinks(help.cy, help.url)
          )
        } else {
          Seq(
            ListLinks(home.en, home.url),
            ListLinks(account.en, account.url),
            ListLinks(message.en, message.url, Some(notificationBadgeCount(message.alerts.getOrElse(0)))),
            ListLinks(help.en, help.url)
          )
        }
      case None => Seq()
    }
}
