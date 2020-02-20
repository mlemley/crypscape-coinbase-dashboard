package app.lemley.crypscape.model

import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Product
import com.google.gson.Gson

data class MarketConfiguration(
    val product: Product,
    val granularity: Granularity
) {

    val remoteProductId:String get() = product.serverId

    companion object {
        fun fromJson(json: String): MarketConfiguration {
            return Gson().fromJson(json, MarketConfiguration::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}