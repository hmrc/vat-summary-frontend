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
    features.showUserResearchBannerEnabled(false)
    features.futureDateOffsetEnabled(true)
    features.poaActiveFeatureEnabled(true)
    features.annualAccountingFeatureEnabled(false)
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

  "The show user research banner feature" should {

    "return its current state" in {
      features.showUserResearchBannerEnabled() shouldBe false
    }

    "switch to a different state" in {
      features.showUserResearchBannerEnabled(true)
      features.showUserResearchBannerEnabled() shouldBe true
    }
  }

  "The futureDateOffset feature" should {

    "return its current state" in {
      features.futureDateOffsetEnabled() shouldBe true
    }

    "switch to a different state" in {
      features.futureDateOffsetEnabled(false)
      features.futureDateOffsetEnabled() shouldBe false
    }
  }

  "The poaActiveFeatureEnabled feature" should {

    "return its current state" in {
      features.poaActiveFeatureEnabled() shouldBe true
    }

    "switch to a different state" in {
      features.poaActiveFeatureEnabled(false)
      features.poaActiveFeatureEnabled() shouldBe false
    }
  }

  "The annualAccountingFeatureEnabled feature" should {

    "return its current state" in {
      features.annualAccountingFeatureEnabled() shouldBe false
    }

    "switch to a different state" in {
      features.annualAccountingFeatureEnabled(true)
      features.annualAccountingFeatureEnabled() shouldBe true
    }
  }
}
