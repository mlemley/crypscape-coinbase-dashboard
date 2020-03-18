package app.lemley.crypscape.ui.market

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import app.lemley.crypscape.R
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.extensions.app.withView
import app.lemley.crypscape.extensions.configureForCrypScape
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.model.currency.toUsd
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.charts.CombinedChart
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class MarketFragment : Fragment() {

    private var chart: CombinedChart? = null
    private var granularity: TabLayout? = null
    private var currencyValue: TextView? = null
    private var currencyPercentChange: TextView? = null
    private var connectionStateView: View? = null
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

    val candleObserver: Observer<List<Candle>> = Observer { candles ->
        with(chart) {
            if (candles.isNotEmpty()) {
                chartingManager.performChartingOperation(
                    this,
                    ChartOperations.ConfigureFor(candles.first().granularity)
                )
            }
            chartingManager.performChartingOperation(
                this,
                ChartOperations.RenderCandles(candles.reversed())
            )
        }
    }

    val stateObserver: Observer<MarketState> = Observer { state ->
        lifecycleScope.launchWhenResumed {
            with(state) {
                marketConfiguration?.let { updateMarketConfiguration(it) }
                ticker?.let { updateWithTicker(it) }
                connectionStateView?.let {
                    it.setBackgroundColor(
                        getColor(
                            it.context,
                            if (state.hasRealtimeConnection) R.color.aqua_marine else R.color.warm_pink
                        )
                    )
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_market, container, false)
        view.findViewById<View>(R.id.drawer_menu)?.setOnClickListener { _ ->
            activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
                ?.openDrawer(GravityCompat.START, true)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        chart = withView(R.id.chart)
        chart?.configureForCrypScape(Granularity.Hour)
        currencyValue = withView(R.id.currency_value)
        currencyPercentChange = withView(R.id.currency_change)
        connectionStateView = withView(R.id.connection_state)
        granularity = withView(R.id.granularity)
        marketViewModel.candles.observe(viewLifecycleOwner, candleObserver)
        marketViewModel.state.observe(viewLifecycleOwner, stateObserver)
        marketViewModel.dispatchEvent(MarketEvents.Init)
        granularity?.addOnTabSelectedListener(granularitySelectedListener)
    }

    override fun onPause() {
        super.onPause()
        marketViewModel.candles.removeObserver(candleObserver)
        marketViewModel.state.removeObserver(stateObserver)
    }

    private fun updateMarketConfiguration(marketConfiguration: MarketConfiguration) {
        withView<TextView>(R.id.currency_name)?.text =
            marketConfiguration.productRemoteId.split("-")[0]
        when (marketConfiguration.granularity) {
            Granularity.Minute -> selectTabAt(0)
            Granularity.FiveMinutes -> selectTabAt(1)
            Granularity.FifteenMinutes -> selectTabAt(2)
            Granularity.Hour -> selectTabAt(3)
            Granularity.SixHours -> selectTabAt(4)
            Granularity.Day -> selectTabAt(5)
        }.exhaustive
    }

    private fun selectTabAt(position: Int) = granularity?.getTabAt(position)?.select()

    @SuppressLint("SetTextI18n")
    private fun updateWithTicker(ticker: Ticker) {
        currencyValue?.text = ticker.price.toUsd().toFormattedCurrency()
        ticker.dailyPercentageChange.let { change ->
            if (ticker.isValid) {
                currencyPercentChange?.apply {
                    text = "$change%"
                    setTextAppearance(if (change >= 0) R.style.TextAppearance_RealTime_ChangePercentageUp else R.style.TextAppearance_RealTime_ChangePercentageDown)
                }
            }
        }
    }
}
