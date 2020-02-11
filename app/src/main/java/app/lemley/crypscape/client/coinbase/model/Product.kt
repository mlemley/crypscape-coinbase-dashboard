package app.lemley.crypscape.client.coinbase.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String = "",

    @SerializedName("base_currency")
    val baseCurrency: String = "",

    @SerializedName("quote_currency")
    val quoteCurrency: String = "",

    @SerializedName("base_min_size")
    val baseMinSize: Double = 0.0,

    @SerializedName("base_max_size")
    val baseMaxSize: Double = 0.0,

    @SerializedName("quote_increment")
    val quoteIncrement: Double = 0.0,

    @SerializedName("display_name")
    val displayName: String = "",

    @SerializedName("status")
    val status: String = "",

    @SerializedName("margin_enabled")
    val isMarginEnabled: Boolean = false,

    @SerializedName("status_message")
    val statusMessage: String = "",

    @SerializedName("min_market_funds")
    val minMarketFunds: Double = 0.0,

    @SerializedName("max_market_funds")
    val maxMarketFunds: Double = 0.0,

    @SerializedName("post_only")
    val isPostOnly: Boolean = false,

    @SerializedName("limit_only")
    val isLimitOnly: Boolean = false,

    @SerializedName("cancel_only")
    val isCancelOnly: Boolean = false
)