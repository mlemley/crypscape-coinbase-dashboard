package app.lemley.crypscape.client.coinbase.model

import com.google.gson.annotations.SerializedName


data class Ticker(
    var sequence: Long = 0,
    var time: String = "",
    var side: String = "",
    var type: String = "",

    @SerializedName("trade_id")
    var tradeId: Long = 0,

    @SerializedName("product_id")
    var productId: String = "",

    @SerializedName("last_size")
    var lastSize: Double =  0.0,

    var price: Double = 0.0,
    var bid: Double = 0.0,

    @SerializedName("best_bid")
    var bestBid: Double = 0.0,

    var ask: Double = 0.0,

    @SerializedName("best_ask")
    var bestAsk: Double = 0.0,

    @SerializedName("open_24h")
    var open24h: Double = 0.0,

    var volume: Double = 0.0,
    var size: Double = 0.0
)