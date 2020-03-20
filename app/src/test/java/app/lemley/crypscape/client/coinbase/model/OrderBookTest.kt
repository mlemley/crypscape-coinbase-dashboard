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
                10102.56 to Ask(10102.56, 0.57753524),
                10101.09 to Ask(10101.09, 0.0)
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
                    10202.8 to Ask(10202.8, 0.262567, changed = false, new = true),
                    10202.55 to Ask(10202.55, 0.0, historicalSize = 0.57753524, changed = true),
                    10202.56 to Ask(10202.56, 0.57753524, changed = false)
                ),
                bids = mapOf(
                    10101.8 to Bid(10101.8, 0.162567, changed = false, new = true),
                    10101.10 to Bid(10101.10, 0.1, historicalSize = 0.45054140, changed = true)
                )
            )
        )
    }

    @Test
    fun acknowledges_change_set() {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                10202.8 to Ask(10202.8, 0.262567, changed = true),
                10202.55 to Ask(10202.55, 0.0, changed = false, new = true),
                10202.56 to Ask(10202.56, 0.57753524, changed = false)
            ),
            bids = mapOf(
                10101.8 to Bid(10101.8, 0.162567, changed = false, new = true),
                10101.10 to Bid(10101.10, 0.1, changed = true)
            )
        )

        assertThat(snapshot.acknowledgeChanges()).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    10202.8 to Ask(10202.8, 0.262567, changed = false, new = false),
                    10202.55 to Ask(10202.55, 0.0, changed = false, new = false),
                    10202.56 to Ask(10202.56, 0.57753524, changed = false, new = false)
                ),
                bids = mapOf(
                    10101.8 to Bid(10101.8, 0.162567, changed = false, new = false),
                    10101.10 to Bid(10101.10, 0.1, changed = false, new = false)
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

    @Test
    fun reduces_to_number_of_instances_per_side() {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                2.9 to Ask(2.9, 2.0),
                2.8 to Ask(2.8, 2.0),
                2.7 to Ask(2.7, 2.0),
                2.6 to Ask(2.6, 2.0),
                2.5 to Ask(2.5, 2.0),
                2.4 to Ask(2.4, 2.0),
                2.3 to Ask(2.3, 2.0),
                2.2 to Ask(2.2, 2.0),
                2.1 to Ask(2.1, 2.0),
                2.0 to Ask(2.0, 2.0)
            ),
            bids = mapOf(
                1.9 to Bid(1.9, 2.0),
                1.8 to Bid(1.8, 2.0),
                1.7 to Bid(1.7, 2.0),
                1.6 to Bid(1.6, 2.0),
                1.5 to Bid(1.5, 2.0),
                1.4 to Bid(1.4, 2.0),
                1.3 to Bid(1.3, 2.0),
                1.2 to Bid(1.2, 2.0),
                1.1 to Bid(1.1, 2.0),
                1.0 to Bid(1.0, 2.0)
            )
        )

        assertThat(snapshot.reduceTo(5)).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    2.4 to Ask(2.4, 2.0),
                    2.3 to Ask(2.3, 2.0),
                    2.2 to Ask(2.2, 2.0),
                    2.1 to Ask(2.1, 2.0),
                    2.0 to Ask(2.0, 2.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 2.0),
                    1.8 to Bid(1.8, 2.0),
                    1.7 to Bid(1.7, 2.0),
                    1.6 to Bid(1.6, 2.0),
                    1.5 to Bid(1.5, 2.0)
                )
            )
        )
    }

    @Test
    fun converts_snapshot_to_depth() {
        val snapshot =
            OrderBook.SnapShot(
                productId = "BTC-USD",
                asks = mapOf(
                    10202.8 to Ask(10202.8, 10.0),
                    10202.56 to Ask(10202.56, 10.0),
                    10202.55 to Ask(10202.55, 10.0)
                ),
                bids = mapOf(
                    10101.7 to Bid(10101.7, 1.0),
                    10101.8 to Bid(10101.8, 1.0),
                    10101.10 to Bid(10101.10, 1.0)
                )
            )

        assertThat(snapshot.forDepth()).isEqualTo(
            OrderBook.Depth(
                type = OrderBookType.Depth,
                productId = "BTC-USD",
                asks = mapOf(
                    10202.8 to DepthEntry(Side.Sell, 10202.8, 30.0F),
                    10202.56 to DepthEntry(Side.Sell, 10202.56, 20.0F),
                    10202.55 to DepthEntry(Side.Sell, 10202.55, 10.0F)
                ),
                bids = mapOf(
                    10101.10 to DepthEntry(Side.Buy, 10101.10, 1.0F),
                    10101.7 to DepthEntry(Side.Buy, 10101.7, 2.0F),
                    10101.8 to DepthEntry(Side.Buy, 10101.8, 3.0F)
                )
            )
        )
    }
}