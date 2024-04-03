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

package config.features

import controllers.ControllerBaseSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration

class FeatureSpec extends ControllerBaseSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {

  private val features = new Features()(app.injector.instanceOf[Configuration])

  override def beforeEach(): Unit = {
    super.beforeEach()
    features.staticDateEnabled(true)
    features.overdueTimeToPayDescriptionEnabled(true)
    features.webchatEnabled(true)
  }

  "The static date feature" should {

    "return its current state" in {
      features.staticDateEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.staticDateEnabled(false)
      features.staticDateEnabled() shouldBe false
    }
  }

  "The overdue label description feature" should {

    "return its current state" in {
      features.overdueTimeToPayDescriptionEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.overdueTimeToPayDescriptionEnabled(false)
      features.overdueTimeToPayDescriptionEnabled() shouldBe false
    }
  }

  "The webchat feature" should {
    "return its current state" in {
      features.webchatEnabled() shouldBe true
    }

    "switch to a different state" in {
      features.webchatEnabled(false)
      features.webchatEnabled() shouldBe false
    }
  }
}
