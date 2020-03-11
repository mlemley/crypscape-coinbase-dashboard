package app.lemley.crypscape.ui.order

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.app.CoroutineContextProvider
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter

@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookViewModel constructor(
    private val coinBaseRealTimeRepository: CoinBaseRealTimeRepository,
    private val contextProvider: CoroutineContextProvider,
    defaultMarketDataRepository: DefaultMarketDataRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            productId = withContext(contextProvider.IO) {
                defaultMarketDataRepository.loadDefault().productRemoteId
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var productId: String? = null
        set(value) {
            field = value
            viewModelScope.launch {
                value?.let {
                    coinBaseRealTimeRepository.subscribe(
                        listOf(it),
                        listOf(Subscribe.Channel.Level2)
                    )
                }
            }
        }

    val orderBookState: LiveData<OrderBook> = coinBaseRealTimeRepository.orderBookFlow
        .filter {
            it.productId == productId
        }
        .catch {
            Crashlytics.logException(it)
            it.printStackTrace()
        }
        .asLiveData(viewModelScope.coroutineContext)

}
