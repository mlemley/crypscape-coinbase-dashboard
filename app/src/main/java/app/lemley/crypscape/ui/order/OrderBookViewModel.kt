package app.lemley.crypscape.ui.order

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.app.CoroutineContextProvider
import app.lemley.crypscape.client.coinbase.model.*
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookViewModel constructor(
    private val coinBaseRealTimeRepository: CoinBaseRealTimeRepository,
    private val contextProvider: CoroutineContextProvider,
    defaultMarketDataRepository: DefaultMarketDataRepository
) : ViewModel() {

    private val pauseTime: Long = 250
    private val maxSizePerSide: Int = 50

    private var fullSnapShot: OrderBook.SnapShot = OrderBook.SnapShot(productId = "BTC-USD")
        @Synchronized
        set

    private val mergeFlow = flow {
        while (true) {
            emit(fullSnapShot.reduceTo(maxSizePerSide))
            fullSnapShot = fullSnapShot.clearEmpty().acknowledgeChanges()
            delay(pauseTime)
        }
    }.flowOn(Dispatchers.IO)

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
                .flowOn(Dispatchers.IO)
                .catch {
                    Crashlytics.logException(it)
                    it.printStackTrace()
                }
                .collect()
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mergeFlow.collect {
                    orderBookChannel.offer(it)
                }
            }
        }
    }

    private val orderBookChannel = ConflatedBroadcastChannel<OrderBook.SnapShot>()
    val orderBookState: LiveData<OrderBook.SnapShot> = orderBookChannel
        .asFlow()
        .conflate()
        .distinctUntilChanged()
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

    private suspend fun updateBook(book: OrderBook) {
        when (book) {
            is OrderBook.SnapShot -> updateWithSnapshot(book)
            is OrderBook.L2Update -> updateWithUpdate(book)
        }.exhaustive
    }

    private suspend fun updateWithSnapshot(book: OrderBook.SnapShot) {
        fullSnapShot = book.reduceTo(100)
        orderBookChannel.offer(book.reduceTo(maxSizePerSide))
    }

    private suspend fun updateWithUpdate(update: OrderBook.L2Update) {
        fullSnapShot = fullSnapShot.mergeChanges(update)
    }
}
