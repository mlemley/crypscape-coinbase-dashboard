package app.lemley.crypscape.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.databinding.FragmentDepthChartBinding
import app.lemley.crypscape.extensions.app.toDecimalFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

@FlowPreview
@ExperimentalCoroutinesApi
class DepthChartFragment : Fragment() {

    private val midMarketPriceFormat:String by inject(named("MidMarketPriceFormat"))
    private val depthChartViewModel: DepthChartViewModel by viewModel()
    private val depthChartManager: DepthChartManager by inject()

    lateinit var binding: FragmentDepthChartBinding

    private val stateObserver: Observer<OrderBook.Depth> = Observer {
        updateMidMarketPrice(it.midMarketPrice)
        depthChartManager.performChartingOperation(
            binding.depthChart,
            DepthChartOperations.RenderDepth(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDepthChartBinding.inflate(inflater)
        depthChartManager.performChartingOperation(
            binding.depthChart,
            DepthChartOperations.Configure
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        depthChartViewModel.depthChartState.observe(viewLifecycleOwner, stateObserver)
    }

    private fun updateMidMarketPrice(price:Double) {
        binding.midMarketPrice.text = price.toDecimalFormat(midMarketPriceFormat)
    }

}
