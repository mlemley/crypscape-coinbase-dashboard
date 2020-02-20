package app.lemley.crypscape.usecase

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class MarketDataUseCase constructor(
    val defaultMarketDataRepository: DefaultMarketDataRepository,
    val coinBaseRepository: CoinBaseRepository
) : UseCase {

    sealed class MarketActions : Action {
        object FetchMarketDataForDefaultConfiguration : MarketActions()

    }

    sealed class MarketResults : Result {
        data class MarketConfigurationResult(val marketConfiguration: MarketConfiguration) :
            MarketResults()

        data class CandlesForConfigurationResult(val candles: Flow<List<Candle>>) : MarketResults()
    }

    override fun canProcess(action: Action): Boolean = action is MarketActions

    override fun handleAction(action: Action): Flow<Result> {
        return when (action) {
            is MarketActions.FetchMarketDataForDefaultConfiguration -> handleFetchDefaultMarketData()
            else -> emptyFlow<Result>()
        }.exhaustive
    }

    private fun handleFetchDefaultMarketData(): Flow<Result> = channelFlow<Result> {
        val marketConfiguration = defaultMarketDataRepository.loadDefault()
        send(MarketResults.MarketConfigurationResult(marketConfiguration))
        send(MarketResults.CandlesForConfigurationResult(coinBaseRepository.candlesForConfiguration(marketConfiguration)))
    }.flowOn(Dispatchers.IO)
}
