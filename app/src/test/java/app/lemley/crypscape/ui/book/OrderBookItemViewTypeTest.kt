package app.lemley.crypscape.ui.book

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OrderBookItemViewTypeTest {

    @Test
    fun provides_view_type_from_id() {
        assertThat(OrderBookItemViewType.typeFromId(0)).isEqualTo(OrderBookItemViewType.Header)
        assertThat(OrderBookItemViewType.typeFromId(1)).isEqualTo(OrderBookItemViewType.Ask)
        assertThat(OrderBookItemViewType.typeFromId(2)).isEqualTo(OrderBookItemViewType.Spread)
        assertThat(OrderBookItemViewType.typeFromId(3)).isEqualTo(OrderBookItemViewType.Bid)
    }


}