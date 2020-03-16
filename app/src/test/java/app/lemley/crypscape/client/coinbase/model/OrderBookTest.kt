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
                asks = mapOf(
                    10102.55 to Ask(10102.55, 0.57753524),
                    10102.56 to Ask(10102.56, 0.57753524)
                ),
                bids = mapOf(
                    10101.10 to Bid(10101.10, 0.45054140),
                    10101.11 to Bid(10101.11, 0.45054140)
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

    @Test
    fun has_calculated_spread() {
        val book = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                10102.55 to Ask(10102.55, 0.57753524),
                10102.56 to Ask(10102.56, 0.57753524)
            ),
            bids = mapOf(
                10101.10 to Bid(10101.10, 0.45054140),
                10101.11 to Bid(10101.11, 0.45054140)
            )
        )

        assertThat(book.spread).isEqualTo(1.4399999999986903)

    }

    @Test
    fun merges_level_2_update_into_snapshot() {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                10202.56 to Ask(10202.56, 0.57753524),
                10202.55 to Ask(10202.55, 0.57753524)
            ),
            bids = mapOf(
                10101.10 to Bid(10101.10, 0.45054140)
            )
        )
        val update = OrderBook.L2Update(
            productId = "BTC-USD",
            time = "2019-08-14T20:42:27.265Z",
            changes = listOf(
                Change(
                    Side.Sell,
                    10202.8,
                    0.262567
                ),
                Change(
                    Side.Sell,
                    10202.55,
                    0.0
                ),
                Change(
                    Side.Buy,
                    10101.8,
                    0.162567
                ),
                Change(
                    Side.Buy,
                    10101.10,
                    0.1
                )
            )
        )

        assertThat(snapshot.mergeChanges(update)).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    10202.8 to Ask(10202.8, 0.262567, true),
                    10202.55 to Ask(10202.55, 0.0, true),
                    10202.56 to Ask(10202.56, 0.57753524, false)
                ),
                bids = mapOf(
                    10101.8 to Bid(10101.8, 0.162567, true),
                    10101.10 to Bid(10101.10, 0.1, true)
                )
            )
        )
    }

    @Test
    fun acknowledges_change_set() {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                10202.8 to Ask(10202.8, 0.262567, true),
                10202.55 to Ask(10202.55, 0.0, true),
                10202.56 to Ask(10202.56, 0.57753524, false)
            ),
            bids = mapOf(
                10101.8 to Bid(10101.8, 0.162567, true),
                10101.10 to Bid(10101.10, 0.1, true)
            )
        )

        assertThat(snapshot.acknowledgeChanges()).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    10202.8 to Ask(10202.8, 0.262567, false),
                    10202.55 to Ask(10202.55, 0.0, false),
                    10202.56 to Ask(10202.56, 0.57753524, false)
                ),
                bids = mapOf(
                    10101.8 to Bid(10101.8, 0.162567, false),
                    10101.10 to Bid(10101.10, 0.1, false)
                )
            )
        )
    }

    @Test
    fun removes_empty_fills_values() {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                10202.8 to Ask(10202.8, 0.262567),
                10202.55 to Ask(10202.55, 0.0),
                10202.56 to Ask(10202.56, 0.57753524)
            ),
            bids = mapOf(
                10101.8 to Bid(10101.8, 0.162567),
                10101.10 to Bid(10101.10, 0.0)
            )
        )

        assertThat(snapshot.clearEmpty()).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    10202.8 to Ask(10202.8, 0.262567),
                    10202.56 to Ask(10202.56, 0.57753524)
                ),
                bids = mapOf(
                    10101.8 to Bid(10101.8, 0.162567)
                )
            )
        )
    }
}