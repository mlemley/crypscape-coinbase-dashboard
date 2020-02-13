package app.lemley.crypscape.client.coinbase.model

import com.google.gson.annotations.SerializedName


data class Currency(
    val id: String = "",
    val name: String = "",
    @SerializedName("min_size")
    val minSize: Double = 0.0,
    val status: String = "",
    val message: String = ""
)

