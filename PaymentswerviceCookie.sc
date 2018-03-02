import java.time.LocalDate

import models.payments.PaymentDetailsModel
import play.api.libs.json.Json

val xx = LocalDate.now().plusMonths(9)
val yy = xx.getMonthValue.formatted("%02d")

val x = LocalDate.now().getMonthValue.formatted("%02d")

val yrr = LocalDate.now().getYear.toString.takeRight(2)

x
xx
yy
yrr


val gg = BigDecimal("25.23")

val ddd = (gg * 100).toInt

println(s"year i:s: $yrr")


val xf = PaymentDetailsModel("vat", "111111", "20000", "04", "18","/ffff/gggg")

val json = Json.toJson(xf)
json

