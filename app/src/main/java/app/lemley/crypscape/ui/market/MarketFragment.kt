package app.lemley.crypscape.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import app.lemley.crypscape.R
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.extensions.app.withView
import app.lemley.crypscape.extensions.app.persistance.baseCurrencyLabel
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.model.currency.toUsd
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.charts.CombinedChart
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class MarketFragment : Fragment() {

    private var chart: CombinedChart? = null
    private var granularity: TabLayout? = null
    private var currencyValue: TextView? = null
    private val marketViewModel: MarketViewModel by viewModel()
    private val chartingManager: MarketChartingManager by inject()

    private val granularitySelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                val granularity = when (it.position) {
                    0 -> Granularity.Minute
                    1 -> Granularity.FiveMinutes
                    2 -> Granularity.FifteenMinutes
                    3 -> Granularity.Hour
                    4 -> Granularity.SixHours
                    5 -> Granularity.Day
                    else -> null
                }

                granularity?.let {
                    marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(it))
                }
            }
        }
    }

    init {
        lifecycleScope.launch {
            whenStarted {
                chart = withView(R.id.chart)
                currencyValue = withView(R.id.currency_value)
                granularity = withView(R.id.granularity)
                marketViewModel.state.observe(viewLifecycleOwner, stateObserver)
                marketViewModel.dispatchEvent(MarketEvents.Init)
            }
            whenResumed {
                granularity?.addOnTabSelectedListener(granularitySelectedListener)
            }
        }
    }

    val candleObserver: Observer<List<Candle>> = Observer { candles ->
        chartingManager.performChartingOperation(chart, ChartOperations.RenderCandles(candles))
    }

    val stateObserver: Observer<MarketState> = Observer { state ->
        lifecycleScope.launchWhenResumed {
            with(state) {
                marketConfiguration?.let { updateMarketConfiguration(it) }
                candles?.asLiveData()?.observe(viewLifecycleOwner, candleObserver)
                ticker?.let { updateWithTicker(it) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)


    private fun updateMarketConfiguration(marketConfiguration: MarketConfiguration) {
        withView<TextView>(R.id.currency_name)?.text = marketConfiguration.product.baseCurrencyLabel
        chartingManager.performChartingOperation(
            chart,
            ChartOperations.ConfigureFor(marketConfiguration.granularity)
        )
    }

    private fun updateWithTicker(ticker: Ticker) {
        currencyValue?.text = ticker.price.toUsd().toFormattedCurrency()
    }
}