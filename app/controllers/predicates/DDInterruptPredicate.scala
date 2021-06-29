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

package controllers.predicates


import javax.inject.Inject
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import common.SessionKeys.viewedDDInterrupt
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future




class DDInterruptPredicate @Inject()(val mcc: MessagesControllerComponents) extends FrontendController(mcc) {

  def interruptCheck(block: Request[AnyContent] => Future[Result])
                    (implicit request: Request[AnyContent]): Future[Result] = {

    if(request.session.get(viewedDDInterrupt).isDefined) {
      block(request)
    } else {
     Future.successful(Redirect(controllers.routes.DDInterruptController.directDebitInterruptCall(request.uri)))
    }
  }
}