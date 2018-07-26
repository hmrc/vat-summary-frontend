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

package config.features

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration

class FeatureSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterEach {

  private val features = new Features(app.injector.instanceOf[Configuration])

  override def beforeEach(): Unit = {
    super.beforeEach()

    features.userResearchBanner(true)
    features.allowDirectDebits(true)
    features.staticDateEnabled(true)
    features.vatCertificateEnabled(true)
    features.enableVatObligationsService(false)
    features.useDirectDebitDummyPage(true)
  }

  "The User Research Banner Feature" should {

    "return its current state" in {
      features.userResearchBanner() mustBe true
    }

    "switch to a new state" in {
      features.userResearchBanner(false)
      features.userResearchBanner() mustBe false
    }
  }

  "The Direct Debits Feature" should {

    "return its current state" in {
      features.allowDirectDebits() mustBe true
    }

    "switch to a new state" in {
      features.allowDirectDebits(false)
      features.allowDirectDebits() mustBe false
    }
  }

  "The Static Date Feature" should {

    "return its current state" in {
      features.staticDateEnabled() mustBe true
    }

    "switch to a new state" in {
      features.staticDateEnabled(false)
      features.staticDateEnabled() mustBe false
    }
  }

  "The Vat Certificate Feature" should {

    "return its current state" in {
      features.vatCertificateEnabled() mustBe true
    }

    "switch to a new state" in {
      features.vatCertificateEnabled(false)
      features.vatCertificateEnabled() mustBe false
    }
  }

  "The Enable Vat Obligations Service Feature" should {

    "return its current state" in {
      features.enableVatObligationsService() mustBe false
    }

    "switch to a new state" in {
      features.enableVatObligationsService(true)
      features.enableVatObligationsService() mustBe true
    }
  }

  "The Direct Debit Dummy Page  Feature" should {

    "return its current state" in {
      features.useDirectDebitDummyPage() mustBe true
    }

    "switch to a new state" in {
      features.useDirectDebitDummyPage(false)
      features.useDirectDebitDummyPage() mustBe false
    }
  }
}
