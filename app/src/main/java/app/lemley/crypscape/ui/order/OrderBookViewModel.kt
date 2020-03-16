package app.lemley.crypscape.ui.order

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.app.CoroutineContextProvider
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.reduceTo
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        viewModelScope.launch {
            coinBaseRealTimeRepository.orderBookFlow
                .filter {
                    it.productId == productId
                }
                .onEach {
                    updateBook(it)
                }
                .catch {
                    Crashlytics.logException(it)
                    it.printStackTrace()
                }
        }
    }

    private val maxSizePerSide: Int = 20
    private var fullSnapShot: OrderBook.SnapShot? = null
    private var partialSnapshot: OrderBook.SnapShot? = null

    private val orderBookChannel = ConflatedBroadcastChannel<OrderBook.SnapShot>()
    val orderBookState: LiveData<OrderBook.SnapShot> = orderBookChannel
        .asFlow()
        .conflate()
        .asLiveData(viewModelScope.coroutineContext)

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


    private fun updateBook(book: OrderBook) {
        when (book) {
            is OrderBook.SnapShot -> updateWithSnapshot(book)
            is OrderBook.L2Update -> TODO()
        }.exhaustive
    }

    private fun updateWithSnapshot(book: OrderBook.SnapShot) {
        fullSnapShot = book
        partialSnapshot = book.reduceTo(maxSizePerSide).also {
            orderBookChannel.offer(it)
        }
    }
}
