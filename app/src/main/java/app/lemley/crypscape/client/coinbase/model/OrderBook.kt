package app.lemley.crypscape.client.coinbase.model

import app.lemley.crypscape.extensions.exhaustive
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

sealed class OrderBookType {
    object SnapShot : OrderBookType() {
        override fun toString(): String {
            return "SnapShot"
        }
    }

    object L2Update : OrderBookType() {
        override fun toString(): String {
            return "L2Update"
        }
    }

    object Depth : OrderBookType() {
        override fun toString(): String {
            return "Depth"
        }
    }
}

sealed class Side {
    object Buy : Side() {
        override fun toString(): String {
            return "buy"
        }
    }

    object Sell : Side() {
        override fun toString(): String {
            return "sell"
        }
    }
}

data class Bid(
    val price: Double,
    val size: Double,
    val historicalSize: Double = 0.0,
    val changed: Boolean = false,
    val new: Boolean = false
)

data class Ask(
    val price: Double,
    val size: Double,
    val historicalSize: Double = 0.0,
    val changed: Boolean = false,
    val new: Boolean = false
)

data class Change(val side: Side, val price: Double, val size: Double)

data class DepthEntry(val side: Side, val price: Double, val depth: Float) {
    override fun toString(): String {
        return "side:$side, price:$price, depth:$depth"
    }
}

interface IOrderBook {
    val productId: String
    val type: OrderBookType
}

sealed class OrderBook : IOrderBook {

    data class Depth(
        override val type: OrderBookType = OrderBookType.Depth,
        override val productId: String,
        val bids: Map<Double, DepthEntry> = emptyMap(),
        val asks: Map<Double, DepthEntry> = emptyMap()
    ) : OrderBook() {

        val midMarketPrice: Double
            get() {
                val minAsk = asks.keys.min() ?: 0.0
                val maxBid = bids.keys.max() ?: 0.0
                return minAsk - ((minAsk - maxBid) / 2.0)
            }

        override fun toString(): String {
            return "type:$type, productId:$productId, bids:$bids, asks:$asks"
        }
    }

    data class SnapShot(
        override val type: OrderBookType = OrderBookType.SnapShot,
        override val productId: String,
        val bids: Map<Double, Bid> = emptyMap(),
        val asks: Map<Double, Ask> = emptyMap()
    ) : OrderBook() {
        fun askAtPosition(i: Int): Ask? {
            return asks[asks.keys.toList()[i]]
        }

        fun bidAtPosition(i: Int): Bid? {
            return bids[bids.keys.toList()[i]]
        }

        val spread: Double
            get() {
                return with(clearEmpty()) {
                    asks.keys.min()?.let { lowestAsk ->
                        bids.keys.max()?.let { highestBid ->
                            lowestAsk - highestBid
                        } ?: 0.0
                    } ?: 0.0
                }
            }
    }

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
        val asks: MutableMap<Double, Ask> = mutableMapOf()
        json.get("asks").asJsonArray.forEach {
            val ask = it.asJsonArray
            asks[ask[0].asDouble] = Ask(ask[0].asDouble, ask[1].asDouble)
        }
        val bids: MutableMap<Double, Bid> = mutableMapOf()
        json.get("bids").asJsonArray.forEach {
            val bid = it.asJsonArray
            bids[bid[0].asDouble] = Bid(bid[0].asDouble, bid[1].asDouble)
        }
        return OrderBook.SnapShot(
            productId = json.get("product_id").asString,
            asks = asks.toSortedMap(reverseOrder()),
            bids = bids.toSortedMap(reverseOrder())
        )
    }

}

fun OrderBook.SnapShot.mergeChanges(change: OrderBook.L2Update): OrderBook.SnapShot {
    val bids = bids.toMutableMap()
    val asks = asks.toMutableMap()

    change.changes.forEach { it ->
        when (it.side) {
            is Side.Buy -> bids[it.price] = Bid(
                it.price,
                it.size,
                bids[it.price]?.size ?: 0.0,
                changed = bids[it.price] != null,
                new = bids[it.price] == null
            )
            is Side.Sell -> asks[it.price] = Ask(
                it.price,
                it.size,
                asks[it.price]?.size ?: 0.0,
                changed = asks[it.price] != null,
                new = asks[it.price] == null
            )
        }.exhaustive
    }
    return copy(asks = asks.toSortedMap(reverseOrder()), bids = bids.toSortedMap(reverseOrder()))
}

fun OrderBook.SnapShot.acknowledgeChanges(): OrderBook.SnapShot = copy(
    asks = (asks.mapValues {
        if (it.value.changed || it.value.new)
            it.value.copy(changed = false, new = false)
        else
            it.value
    }).toSortedMap(reverseOrder()),
    bids = (bids.mapValues {
        if (it.value.changed || it.value.new)
            it.value.copy(changed = false, new = false)
        else
            it.value
    }).toSortedMap(reverseOrder())
)

fun OrderBook.SnapShot.clearEmpty(): OrderBook.SnapShot = copy(
    asks = (asks - asks.values.partition { it.size > 0 }.second.map { it.price }).toSortedMap(
        reverseOrder()
    ),
    bids = (bids - bids.values.partition { it.size > 0 }.second.map { it.price }).toSortedMap(
        reverseOrder()
    )
)

fun OrderBook.SnapShot.reduceTo(maxPerSide: Int): OrderBook.SnapShot = copy(
    asks = if (maxPerSide + 1 < asks.size) (asks - asks.keys.sorted()
        .slice(maxPerSide until asks.size)).toSortedMap(reverseOrder()) else asks,
    bids = if (maxPerSide + 1 < bids.size) (bids - bids.keys.sortedDescending()
        .slice(maxPerSide until bids.size)).toSortedMap(reverseOrder()) else bids
)

fun OrderBook.SnapShot.forDepth(): OrderBook.Depth {
    var size: Float = 0F
    val bidsDepth: MutableMap<Double, DepthEntry> = mutableMapOf()
    bids.toSortedMap(reverseOrder()).values.forEach {
        size += it.size.toFloat()
        bidsDepth[it.price] = DepthEntry(Side.Buy, it.price, size)
    }

    size = 0F
    val askDepth: MutableMap<Double, DepthEntry> = mutableMapOf()
    asks.toSortedMap().values.forEach {
        size += it.size.toFloat()
        askDepth[it.price] = DepthEntry(Side.Sell, it.price, size)
    }

    return OrderBook.Depth(
        productId = this.productId,
        asks = askDepth.toSortedMap(),
        bids = bidsDepth.toSortedMap()
    )
}
