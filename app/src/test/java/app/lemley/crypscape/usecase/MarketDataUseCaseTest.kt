package app.lemley.crypscape.usecase

import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketActions
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketResults
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class MarketDataUseCaseTest {

    private fun createUseCase(
        defaultMarketDataRepository: DefaultMarketDataRepository = mockk(relaxUnitFun = true),
        coinBaseRepository: CoinBaseRepository = mockk(relaxUnitFun = true)

    ): MarketDataUseCase = MarketDataUseCase(defaultMarketDataRepository, coinBaseRepository)

    @Test
    fun can_process_its_actions() {
        val useCase = createUseCase()

        assertThat(useCase.canProcess(MarketActions.FetchMarketDataForDefaultConfiguration)).isTrue()
        assertThat(useCase.canProcess(object : Action {})).isFalse()
    }

    @Test
    fun handles_fetching_default_market_data() {
        val marketConfiguration = MarketConfiguration(mockk(), mockk())
        val defaultMarketDataRepository: DefaultMarketDataRepository = mockk {
            every { runBlocking { loadDefault() } } returns marketConfiguration
        }

        val candles = flowOf<List<Candle>>(mockk(), mockk())
        val ticker:Ticker = Ticker()
        val coinBaseRepository: CoinBaseRepository = mockk {
            every { runBlocking { candlesForConfiguration(marketConfiguration) } } returns candles
            every { runBlocking { tickerForConfiguration(marketConfiguration) } } returns ticker
        }
        val useCase = createUseCase(defaultMarketDataRepository, coinBaseRepository)

        val results = mutableListOf<Result>()
        runBlocking {
            val result = useCase.handleAction(MarketActions.FetchMarketDataForDefaultConfiguration)
            result.toList(results)
        }

        assertThat(results).isEqualTo(
            listOf(
                MarketResults.MarketConfigurationResult(marketConfiguration),
                MarketResults.TickerResult(ticker),
                MarketResults.CandlesForConfigurationResult(candles)
            )
        )
    }

    @Test
    fun handles_change_of_granularity() {
        val granularity = Granularity.FiveMinutes
        val marketConfiguration = MarketConfiguration(mockk(), mockk())
        val defaultMarketDataRepository: DefaultMarketDataRepository = mockk {
            every { runBlocking { changeGranularity(granularity) } } returns marketConfiguration
        }

        val candles = flowOf<List<Candle>>(mockk(), mockk())
        val ticker:Ticker = Ticker()
        val coinBaseRepository: CoinBaseRepository = mockk {
            every { runBlocking { candlesForConfiguration(marketConfiguration) } } returns candles
            every { runBlocking { tickerForConfiguration(marketConfiguration) } } returns ticker
        }
        val useCase = createUseCase(defaultMarketDataRepository, coinBaseRepository)

        val results = mutableListOf<Result>()
        runBlocking {
            val result = useCase.handleAction(MarketActions.OnGranularityChanged(granularity))
            result.toList(results)
        }

        assertThat(results).isEqualTo(
            listOf(
                MarketResults.MarketConfigurationResult(marketConfiguration),
                MarketResults.TickerResult(ticker),
                MarketResults.CandlesForConfigurationResult(candles)
            )
        )
    }
}