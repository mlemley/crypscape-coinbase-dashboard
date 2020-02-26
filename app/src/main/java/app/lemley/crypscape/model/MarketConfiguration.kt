package app.lemley.crypscape.model

import app.lemley.crypscape.persistance.entities.Granularity
import com.google.gson.Gson

data class MarketConfiguration(
    val platformId: Long,
    val productRemoteId: String,
    val granularity: Granularity
) {

    companion object {
        fun fromJson(json: String): MarketConfiguration {
            return Gson().fromJson(json, MarketConfiguration::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
