/*
 * Copyright 2022 HM Revenue & Customs
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
    features.userResearchBanner(true)
    features.penaltiesServiceEnabled(true)
    features.staticDateEnabled(true)
    features.directDebitInterrupt(true)
    features.interestBreakdownEnabled(true)
    features.chargeReferenceInsetEnabled(true)
  }

  "The User Research Banner Feature" should {

    "return its current state" in {
      features.userResearchBanner() shouldBe true
    }

    "switch to a new state" in {
      features.userResearchBanner(false)
      features.userResearchBanner() shouldBe false
    }
  }

  "The Penalties Service feature" should {

    "return its current state" in {
      features.penaltiesServiceEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.penaltiesServiceEnabled(false)
      features.penaltiesServiceEnabled() shouldBe false
    }
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

  "The direct debit interrupt feature" should {

    "return its current state" in {
      features.directDebitInterrupt() shouldBe true
    }

    "switch to a new state" in {
      features.directDebitInterrupt(false)
      features.directDebitInterrupt() shouldBe false
    }
  }

  "The interest breakdown feature" should {

    "return its current state" in {
      features.interestBreakdownEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.interestBreakdownEnabled(false)
      features.interestBreakdownEnabled() shouldBe false
    }
  }

  "The charge reference inset text feature" should {

    "return its current state" in {
      features.chargeReferenceInsetEnabled() shouldBe true
    }

    "switch to a new state" in {
      features.chargeReferenceInsetEnabled(false)
      features.chargeReferenceInsetEnabled() shouldBe false
    }
  }
}
