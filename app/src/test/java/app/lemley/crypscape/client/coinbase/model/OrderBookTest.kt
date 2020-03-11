package app.lemley.crypscape.client.coinbase.model

import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import org.junit.Test

class OrderBookTest {

    @Test
    fun deserialize_snapshot() {
        val builder = GsonBuilder()
        val orderBookDeserializer = OrderBookDeserializer()
        builder.registerTypeAdapter(OrderBook::class.java, orderBookDeserializer)
        assertThat(
            builder.create().fromJson(TestData.orderBookSnapshot, OrderBook::class.java)
        ).isEqualTo(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                asks = listOf(
                    Ask(10102.55, 0.57753524)
                ),
                bids = listOf(
                    Bid(10101.10, 0.45054140)
                )
            )
        )
    }

    @Test
    fun deserialize_level_2_update() {
        val builder = GsonBuilder()
        val orderBookDeserializer = OrderBookDeserializer()
        builder.registerTypeAdapter(OrderBook::class.java, orderBookDeserializer)
        assertThat(
            builder.create().fromJson(TestData.orderBookL2Update, OrderBook::class.java)
        ).isEqualTo(
            OrderBook.L2Update(
                productId = "BTC-USD",
                time = "2019-08-14T20:42:27.265Z",
                changes = listOf(
                    Change(
                        Side.Buy,
                        10101.8,
                        0.162567
                    ),
                    Change(
                        Side.Sell,
                        10202.8,
                        0.262567
                    )
                )
            )
        )
    }

}