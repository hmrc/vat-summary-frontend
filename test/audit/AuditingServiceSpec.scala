/*
 * Copyright 2018 HM Revenue & Customs
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

package audit

import config.FrontendAuditConnector
import controllers.ControllerBaseSpec
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.http.HeaderCarrier

class AuditingServiceSpec extends ControllerBaseSpec with BeforeAndAfterEach {

  private trait Test {

    lazy val mockAuditConnector: FrontendAuditConnector = mock[FrontendAuditConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def setupMocks(): Unit

    def target(): AuditingService = {
      setupMocks()
      new AuditingService(mockAppConfig, mockAuditConnector)
    }
  }
}
