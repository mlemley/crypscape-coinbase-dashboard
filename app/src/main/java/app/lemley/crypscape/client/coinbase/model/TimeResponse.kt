package app.lemley.crypscape.client.coinbase.model

import java.math.BigDecimal
import java.math.RoundingMode

data class TimeResponse(
    var iso: String,
    var epoch: BigDecimal
) {
    val epochAsMillis: Long get() = epoch.setScale(3, RoundingMode.HALF_UP)
        .movePointRight(3).longValueExact()
}
