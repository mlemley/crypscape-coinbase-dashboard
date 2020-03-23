package app.lemley.crypscape.ui.book

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
import app.lemley.crypscape.extensions.app.toDecimalFormat
import app.lemley.crypscape.ui.base.recyclerview.StickyHeaderDecoration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named


@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookFragment : Fragment() {

    private val orderBookViewModel: OrderBookViewModel by viewModel()
    private val orderBookAdapter: OrderBookAdapter by inject()
    private val depthChartViewModel: DepthChartViewModel by viewModel()
    private val depthChartManager: DepthChartManager by inject()
    private val midMarketPriceFormat: String by inject(named("MidMarketPriceFormat"))

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var binding: FragmentOrderBookBinding

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val orderBookStateObserver: Observer<in OrderBook.SnapShot> = Observer {
        if (orderBookAdapter.isEmpty()) {
            orderBookAdapter.updateWith(it)
            binding.orderBook.layoutManager?.scrollToPosition(orderBookAdapter.spreadPosition + 21)
        } else {
            orderBookAdapter.updateWith(it)
        }
    }

    val depthChartStateObserver: Observer<OrderBook.Depth> = Observer { state ->
        binding.depthChart?.let { chart ->
            updateMidMarketPrice(state.midMarketPrice)
            depthChartManager.performChartingOperation(
                chart,
                DepthChartOperations.RenderDepth(state)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBookBinding.inflate(layoutInflater)
        val view = binding.root
        setupOrderBook()
        setupDepthChart()
        return view
    }

    override fun onResume() {
        super.onResume()
        orderBookViewModel.orderBookState.observe(viewLifecycleOwner, orderBookStateObserver)
        orderBookViewModel.depthChartState.observe(viewLifecycleOwner, depthChartStateObserver)
    }

    override fun onPause() {
        super.onPause()
        orderBookViewModel.orderBookState.removeObserver(orderBookStateObserver)
    }

    private fun setupOrderBook() {
        binding.orderBook.apply {
            adapter = orderBookAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(false)
            addItemDecoration(
                StickyHeaderDecoration(
                    orderBookAdapter
                )
            )
        }
    }

    private fun setupDepthChart() {
        depthChartManager.performChartingOperation(
            binding.depthChart,
            DepthChartOperations.Configure
        )
    }

    private fun updateMidMarketPrice(price: Double) {
        binding.midMarketPrice?.text = price.toDecimalFormat(midMarketPriceFormat)
    }


}