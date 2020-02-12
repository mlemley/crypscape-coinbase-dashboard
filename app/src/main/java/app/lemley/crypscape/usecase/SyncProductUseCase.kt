package app.lemley.crypscape.usecase

import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*


@ExperimentalCoroutinesApi
class SyncProductUseCase(
    val coinBaseRepository: CoinBaseRepository
) : UseCase {

    object SyncProductData : Action
    object ProductSyncComplete : Result

    override fun canProcess(action: Action): Boolean = action == SyncProductData

    override fun handleAction(action: Action): Flow<Result> = when (action) {
            is SyncProductData -> syncProductData()
            else -> emptyFlow()
        }

    private fun syncProductData(): Flow<Result> = channelFlow {
        coinBaseRepository.syncProducts()
        send(ProductSyncComplete)
    }.flowOn(Dispatchers.IO)

}