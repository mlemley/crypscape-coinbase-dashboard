package app.lemley.crypscape.ui.market

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.rules.TestContextProvider
import app.lemley.crypscape.rules.TestCoroutineRule
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.usecase.MarketDataUseCase
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketActions
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private fun createViewModel(
        marketDataUseCase: MarketDataUseCase = mockk(relaxUnitFun = true),
        coinbaseRepository: CoinBaseRepository = mockk(relaxUnitFun = true),
        coinBaseRealTimeRepository: CoinBaseRealTimeRepository = mockk(relaxed = true)
    ): MarketViewModel = MarketViewModel(
        marketDataUseCase,
        coinBaseRepository = coinbaseRepository,
        coinBaseRealTimeRepository = coinBaseRealTimeRepository,
        contextProvider = TestContextProvider()
    )

    @Test
    fun has_expected_use_cases() {
        val marketDataUseCase: MarketDataUseCase = mockk(relaxUnitFun = true)
        val viewModel = createViewModel(marketDataUseCase)

        assertThat(viewModel.useCases).isEqualTo(
            listOf(
                marketDataUseCase
            )
        )
    }

    @Test
    fun maps_events_to_actions() {
        val viewModel = createViewModel()
        val ticker = Ticker(price = 9_898.00)

        val events = flowOf(
            MarketEvents.Init,
            MarketEvents.GranularitySelected(Granularity.Minute),
            MarketEvents.TickerChangedEvent(ticker),
            MarketEvents.RealtimeConnectionChangedEvent(true)
        )

        val expectedActions = listOf(
            MarketActions.FetchMarketDataForDefaultConfiguration,
            MarketActions.OnGranularityChanged(Granularity.Minute),
            MarketActions.OnTickerTick(ticker),
            MarketActions.OnConnectionChanged(true)
        )

        val actual = mutableListOf<Action>()
        with(viewModel) {
            runBlocking {
                events.eventTransform().toList(actual)
            }
        }

        assertThat(actual).isEqualTo(expectedActions)
    }

    @Test
    fun plus__merges_market_configuration() = testCoroutineRule.runBlockingTest {
        val viewModel = createViewModel()
        val initState = viewModel.makeInitState()

        val marketConfiguration = MarketConfiguration(
            1L,
            "BTC-USD",
            Granularity.Hour
        )
        val ticker: Ticker = mockk()

        val results = listOf(
            MarketDataUseCase.MarketResults.MarketConfigurationResult(marketConfiguration),
            MarketDataUseCase.MarketResults.TickerResult(ticker),
            MarketDataUseCase.MarketResults.RealTimeConnectionChange(true)
        )

        val expectedStates: List<MarketState> = listOf(
            initState.copy(marketConfiguration = marketConfiguration),
            initState.copy(ticker = ticker),
            initState.copy(hasRealtimeConnection = true)
        )

        val actual = mutableListOf<MarketState>()
        results.forEach {
            with(viewModel) {
                actual.add(initState + it)
            }
        }

        assertThat(actual).isEqualTo(expectedStates)
    }
}
