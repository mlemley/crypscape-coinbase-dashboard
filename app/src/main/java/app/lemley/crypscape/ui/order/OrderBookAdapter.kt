package app.lemley.crypscape.ui.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.lemley.crypscape.client.coinbase.model.OrderBook

class OrderBookAdapter constructor(
    private val orderBookDataManager: OrderBookDataManager
) : RecyclerView.Adapter<OrderBookViewItemHolder>() {

    fun updateWith(orderBook: OrderBook) {
        orderBookDataManager.updateData(orderBook)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = orderBookDataManager.size

    override fun getItemViewType(position: Int): Int =
        orderBookDataManager.typeForPosition(position).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBookViewItemHolder {
        val orderBookItemViewType = OrderBookItemViewType.typeFromId(viewType)
        return orderBookDataManager.holderForType(orderBookItemViewType, parent)
    }

    override fun onBindViewHolder(holder: OrderBookViewItemHolder, position: Int) {
        orderBookDataManager.bindToHolder(holder, position)
    }

}
