package app.lemley.crypscape.usecasei

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MarketDataUseCase : UseCase {

    sealed class MarketActions : Action {
        object FetchMarketDataForDefaultConfiguration: MarketActions()

    }

    override fun canProcess(action: Action): Boolean = false

    override fun handleAction(action: Action): Flow<Result> {
        return when(action) {

            else -> emptyFlow<Result>()
        }.exhaustive
    }
}
