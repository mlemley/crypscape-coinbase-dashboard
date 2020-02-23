package app.lemley.crypscape.client.coinbase.model

data class CandleRequest(
    val productId:String,
    val granularity: Granularity,
    val start: String? = null,
    val end: String? = null
) {
    fun asMap(): Map<String, String> = mutableMapOf(
        Pair("granularity", granularity.seconds.toString())
    ).also {
        if (!start.isNullOrEmpty() && !end.isNullOrEmpty()) {
            it.put("start", start)
            it.put("end", end)
        }
    }
}
