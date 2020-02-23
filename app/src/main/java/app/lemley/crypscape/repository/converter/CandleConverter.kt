package app.lemley.crypscape.repository.converter

import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity


class CandleConverter {
    fun convert(
        platformId: Long,
        productId: Long,
        granularity: Granularity,
        candle: Array<Double>
    ): Candle = Candle(
        platform_id = platformId,
        product_id = productId,
        granularity = granularity,
        time = candle[0].toInstant(),
        low = candle[1],
        high = candle[2],
        open = candle[3],
        close = candle[4],
        volume = candle[5]
    )
}