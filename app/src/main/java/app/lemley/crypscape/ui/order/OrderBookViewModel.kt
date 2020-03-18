package app.lemley.crypscape.ui.order

import android.util.Log
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
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookViewModel constructor(
    private val coinBaseRealTimeRepository: CoinBaseRealTimeRepository,
    private val contextProvider: CoroutineContextProvider,
    private val defaultMarketDataRepository: DefaultMarketDataRepository
) : ViewModel() {

    private val pauseTime: Long = 250
    private val maxSizePerSide: Int = 50

    @Volatile
    private var fullSnapShot: OrderBook.SnapShot = OrderBook.SnapShot(productId = "BTC-USD")

    @Synchronized
    private fun setFullSnapShot(snapshot: OrderBook.SnapShot) {
        fullSnapShot = snapshot
    }

    private val mergeFlow get() = flow {
        while (true) {
            emit(fullSnapShot.reduceTo(maxSizePerSide))
            setFullSnapShot(fullSnapShot.clearEmpty().acknowledgeChanges())
            delay(pauseTime)
        }
    }.flowOn(Dispatchers.IO)

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            productId = withContext(contextProvider.IO) {
                defaultMarketDataRepository.loadDefault().productRemoteId
            }
        }
        GlobalScope.launch {
            coinBaseRealTimeRepository.orderBookFlow
                .filter {
                    it.productId == productId
                }
                .collect {
                    updateBook(it)
                }
        }

        viewModelScope.launch {
            mergeFlow.collect {
                if (fullSnapShot.spread < 0) {
                    productId?.let {
                        Log.w(
                            "-- SPREAD --",
                            "--- Value of spread went negative resubscribing --- ${fullSnapShot.spread}"
                        )
                        subscribeToLevel2(it)
                    }
                }
                orderBookChannel.offer(it)
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
            value?.let {
                subscribeToLevel2(it)
            }
        }

    private fun subscribeToLevel2(productId: String) {
        viewModelScope.launch {
            coinBaseRealTimeRepository.subscribe(
                listOf(productId),
                listOf(Subscribe.Channel.Level2)
            )
        }
    }

    private fun updateBook(book: OrderBook) {
        when (book) {
            is OrderBook.SnapShot -> updateWithSnapshot(book)
            is OrderBook.L2Update -> updateWithUpdate(book)
        }.exhaustive
    }

    private fun updateWithSnapshot(book: OrderBook.SnapShot) {
        setFullSnapShot(book.reduceTo(100))
        orderBookChannel.offer(book.reduceTo(maxSizePerSide))
    }

    private fun updateWithUpdate(update: OrderBook.L2Update) {
        setFullSnapShot(fullSnapShot.mergeChanges(update))
    }
}
