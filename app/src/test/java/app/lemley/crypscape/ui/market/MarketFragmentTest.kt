package app.lemley.crypscape.ui.market

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers.loadModules
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.android.synthetic.main.fragment_home.*
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
        val marketViewModel: MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
        }

        excludeRecords {
            marketViewModel.state
        }

        createFragmentScenario(marketViewModel = marketViewModel).onFragment { fragment ->
            verifyOrder {
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
                marketViewModel.dispatchEvent(MarketEvents.Init)
            }
        }

        confirmVerified(liveDataState, marketViewModel)
    }


    @Test
    fun on_state_change__sets_currency_name() {
        val product: Product = mockk(relaxed = true) {
            every { serverId } returns "BTC-USD"
        }
        createFragmentScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(
                MarketState(
                    MarketConfiguration(
                        product = product,
                        granularity = Granularity.Hour
                    )
                )
            )

            assertThat(fragment.currency_name.text).isEqualTo("BTC")
        }
    }

    @Test
    fun on_state_change__configures_charts_granularity() {
        val product: Product = mockk(relaxed = true) {
            every { serverId } returns "BTC-USD"
        }
        val marketChartingManager: MarketChartingManager = mockk(relaxUnitFun = true)
        createFragmentScenario(marketChartingManager = marketChartingManager).onFragment { fragment ->
            fragment.stateObserver.onChanged(
                MarketState(
                    MarketConfiguration(
                        product = product,
                        granularity = Granularity.Hour
                    )
                )
            )

            verify {
                marketChartingManager.performChartingOperation(
                    fragment.chart,
                    ChartOperations.ConfigureFor(Granularity.Hour)
                )
            }
        }

        confirmVerified(marketChartingManager)
    }

    @Test
    fun on_state_change__renders_candles() {
        val candles = emptyList<Candle>()
        val marketChartingManager: MarketChartingManager = mockk(relaxUnitFun = true)
        createFragmentScenario(marketChartingManager = marketChartingManager).onFragment { fragment ->
            fragment.candleObserver.onChanged(candles)

            verify {
                marketChartingManager.performChartingOperation(
                    fragment.chart,
                    ChartOperations.RenderCandles(candles)
                )
            }
        }

        confirmVerified(marketChartingManager)
    }
}