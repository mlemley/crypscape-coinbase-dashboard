package app.lemley.crypscape.ui.market

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers.loadModules
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.android.synthetic.main.fragment_market.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MarketFragmentTest {

    private fun createFragmentScenario(
        liveDataState: LiveData<MarketState> = mockk(relaxUnitFun = true),
        marketViewModel: MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
            every { candles } returns mockk(relaxUnitFun = true)
        },
        marketChartingManager: MarketChartingManager = mockk(relaxUnitFun = true)
    ): FragmentScenario<MarketFragment> {
        loadModules(module {
            viewModel { marketViewModel }
            factory { marketChartingManager }
        })

        return FragmentScenario.launchInContainer(MarketFragment::class.java)
    }

    @Test
    fun observes_state__broadcasts_init_event() {
        val liveDataState: LiveData<MarketState> = mockk(relaxUnitFun = true)
        val candleLiveData: LiveData<List<Candle>> = mockk(relaxUnitFun = true)
        val marketViewModel: MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
            every { candles } returns candleLiveData
        }

        excludeRecords {
            marketViewModel.state
            marketViewModel.candles
        }

        createFragmentScenario(marketViewModel = marketViewModel).onFragment { fragment ->
            verifyOrder {
                candleLiveData.observe(fragment.viewLifecycleOwner, fragment.candleObserver)
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
                marketViewModel.dispatchEvent(MarketEvents.Init)
            }
        }

        confirmVerified(liveDataState, marketViewModel)
    }


    @Test
    fun on_state_change__sets_currency_name() {
        createFragmentScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(
                MarketState(
                    MarketConfiguration(
                        platformId = 1L,
                        productRemoteId = "BTC-USD",
                        granularity = Granularity.Hour
                    )
                )
            )

            assertThat(fragment.currency_name.text).isEqualTo("BTC")
        }
    }

    @Test
    fun on_state_change__renders_candles() {
        val candles = listOf<Candle>(mockk {
            every { granularity } returns Granularity.Hour
        })
        val marketChartingManager: MarketChartingManager = mockk(relaxUnitFun = true)
        createFragmentScenario(marketChartingManager = marketChartingManager).onFragment { fragment ->
            fragment.candleObserver.onChanged(candles)

            verifyOrder {
                marketChartingManager.performChartingOperation(
                    fragment.chart,
                    ChartOperations.Clear
                )
                marketChartingManager.performChartingOperation(
                    fragment.chart,
                    ChartOperations.ConfigureFor(Granularity.Hour)
                )
                marketChartingManager.performChartingOperation(
                    fragment.chart,
                    ChartOperations.RenderCandles(candles)
                )
            }
        }

        confirmVerified(marketChartingManager)
    }

    @Test
    fun on_state_change__renders_ticker() {
        val ticker = Ticker(price = 10000.00)
        createFragmentScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(MarketState(ticker = ticker))

            assertThat(fragment.currency_value.text).isEqualTo("$10,000.00")
        }
    }

    @Test
    fun selects_tab_when_initially_loaded() {
        createFragmentScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(MarketState(marketConfiguration = mockk {
                every { granularity } returns Granularity.Hour
                every { productRemoteId } returns "BTC-USD"
            }))

            assertThat(fragment.granularity?.selectedTabPosition).isEqualTo(3)
        }
    }

    @Test
    fun changes_granularity_when_selection_changes() {
        val liveDataState: LiveData<MarketState> = mockk(relaxUnitFun = true)
        val marketViewModel: MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
            every { candles } returns mockk(relaxUnitFun = true)
        }

        excludeRecords {
            marketViewModel.state
            marketViewModel.dispatchEvent(MarketEvents.Init)
        }
        createFragmentScenario(marketViewModel = marketViewModel).onFragment { fragment ->
            fragment.granularity.getTabAt(1)?.select()
            fragment.granularity.getTabAt(2)?.select()
            fragment.granularity.getTabAt(3)?.select()
            fragment.granularity.getTabAt(4)?.select()
            fragment.granularity.getTabAt(5)?.select()
            fragment.granularity.getTabAt(0)?.select()

        }

        verifyOrder {
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.FiveMinutes))
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.FifteenMinutes))
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.Hour))
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.SixHours))
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.Day))
            marketViewModel.dispatchEvent(MarketEvents.GranularitySelected(Granularity.Minute))
        }
    }
}
