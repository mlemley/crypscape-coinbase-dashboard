package app.lemley.crypscape.client.coinbase.model

data class CandleRequest(
    val product: Product,
    val granularity: Granularity,
    val start: String,
    val end: String
) {
    fun asMap(): Map<String, String> = mapOf(
        Pair("granularity", granularity.seconds.toString()),
        Pair("start", start),
        Pair("end", end)
    )
}
