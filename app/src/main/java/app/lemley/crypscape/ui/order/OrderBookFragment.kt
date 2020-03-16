package app.lemley.crypscape.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.databinding.FragmentOrderBookBinding
import app.lemley.crypscape.extensions.exhaustive
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookFragment : Fragment() {

    private val orderBookViewModel: OrderBookViewModel by viewModel()
    private val orderBookAdapter: OrderBookAdapter by inject()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var binder: FragmentOrderBookBinding

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val orderBookStateObserver: Observer<in OrderBook> = Observer {
        when (it) {
            is OrderBook.SnapShot -> orderBookAdapter.updateWith(it)
            is OrderBook.L2Update -> TODO()
        }.exhaustive
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentOrderBookBinding.inflate(layoutInflater)
        val view = binder.root
        orderBookViewModel.orderBookState.observe(viewLifecycleOwner, orderBookStateObserver)
        setupOrderBook()
        return view
    }

    private fun setupOrderBook() {
        binder.orderBook.apply {
            adapter = orderBookAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
        }
    }

}