package app.lemley.crypscape.client.coinbase.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

sealed class OrderBookType {
    object SnapShot : OrderBookType()
    object L2Update : OrderBookType()
}

sealed class Side {
    object Buy : Side()
    object Sell : Side()
}

data class Bid(val price: Double, val size: Double)
data class Ask(val price: Double, val size: Double)
data class Change(val side: Side, val price: Double, val size: Double)

interface IOrderBook {
    val productId:String
    val type:OrderBookType
}

sealed class OrderBook: IOrderBook {
    data class SnapShot(
        override val type: OrderBookType = OrderBookType.SnapShot,
        override val productId: String,
        val bids: List<Bid> = emptyList(),
        val asks: List<Ask> = emptyList()
    ) : OrderBook()

    data class L2Update(
        override val type: OrderBookType = OrderBookType.L2Update,
        val time: String,
        override val productId: String,
        val changes: List<Change> = emptyList()
    ) : OrderBook()
}

class OrderBookDeserializer : JsonDeserializer<OrderBook> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OrderBook {
        return json?.asJsonObject?.let { jsonObject ->
            return when (jsonObject.get("type").asString) {
                "l2update" -> deserializeLevel2(jsonObject)
                "snapshot" -> deserializeSnapShot(jsonObject)
                else -> throw IllegalArgumentException("Not OrderBook Type  -- received $json")
            }
        } ?: throw IllegalArgumentException("Not OrderBook Type  -- received $json")
    }

    private fun deserializeLevel2(json: JsonObject): OrderBook {
        val changes: MutableList<Change> = mutableListOf()
        json.get("changes").asJsonArray.forEach {
            val change = it.asJsonArray
            changes.add(
                Change(
                    side = when (change[0].asString) {
                        "buy" -> Side.Buy
                        "sell" -> Side.Sell
                        else -> throw IllegalArgumentException(
                            """Expected Side: But received ${json.get(
                                "side"
                            ).asString}"""
                        )
                    },
                    price = change[1].asDouble,
                    size = change[2].asDouble
                )
            )
        }
        return OrderBook.L2Update(
            productId = json.get("product_id").asString,
            time = json.get("time").asString,
            changes = changes

        )
    }

    private fun deserializeSnapShot(json: JsonObject): OrderBook {
        val asks: MutableList<Ask> = mutableListOf()
        json.get("asks").asJsonArray.forEach {
            val ask = it.asJsonArray
            asks.add(Ask(ask[0].asDouble, ask[1].asDouble))
        }
        val bids: MutableList<Bid> = mutableListOf()
        json.get("bids").asJsonArray.forEach {
            val bid = it.asJsonArray
            bids.add(Bid(bid[0].asDouble, bid[1].asDouble))
        }
        return OrderBook.SnapShot(
            productId = json.get("product_id").asString,
            asks = asks,
            bids = bids
        )
    }

}
