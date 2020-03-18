package app.lemley.crypscape.app

import android.app.Application
import android.util.Log
import app.lemley.crypscape.app.di.appModules
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository.ConnectionState
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
open class CrypScapeApplication : Application() {


    val coinBaseRealTimeRepository: CoinBaseRealTimeRepository by inject()
    val coinBaseWSService: CoinBaseWSService by inject()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        loadKoin()
        startSocketIO()
    }

    protected open fun loadKoin() {
        startKoin {
            androidLogger()
            androidContext(this@CrypScapeApplication)
            modules(appModules)
        }
    }

    private fun startSocketIO() {
        var currentSubscription: Subscribe? = null
        GlobalScope.launch {
            coinBaseRealTimeRepository.subscriptionChannel.asFlow()
                .collect { subscription ->
                    subscription.current?.let { current ->
                        if (coinBaseRealTimeRepository.connectionStateChannel.valueOrNull == ConnectionState.Connected) {
                            currentSubscription?.let {
                                coinBaseWSService.sendSubscribe(it.copy(type = Subscribe.Type.Unsubscribe.toString()))
                            }
                            coinBaseWSService.sendSubscribe(current)
                            currentSubscription = current
                        }
                    }
                }
        }

        GlobalScope.launch {
            coinBaseWSService.observeWebSocketEvent().consumeAsFlow()
                .flowOn(Dispatchers.IO)
                .onEach {
                    Log.v("Web Socket Event:", it.toString())
                    when {
                        it is WebSocket.Event.OnConnectionOpened<Any> ->
                            coinBaseRealTimeRepository.connectionStateChannel.offer(
                                ConnectionState.Connected
                            )
                        it is WebSocket.Event.OnConnectionClosed ->
                            coinBaseRealTimeRepository.connectionStateChannel.offer(
                                ConnectionState.Disconnected
                            )
                        it is WebSocket.Event.OnMessageReceived -> {
                            val latest =
                                coinBaseRealTimeRepository.subscriptionChannel.valueOrNull?.current
                            if (currentSubscription != latest) {
                                currentSubscription?.let {
                                    coinBaseWSService.sendSubscribe(it.copy(type = Subscribe.Type.Unsubscribe.toString()))
                                }
                                latest?.let {
                                    coinBaseWSService.sendSubscribe(it)
                                    currentSubscription = latest
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .filter {
                    it is WebSocket.Event.OnConnectionOpened<Any>
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    val latest = coinBaseRealTimeRepository.subscriptionChannel.valueOrNull?.current
                    if (currentSubscription != latest) {
                        currentSubscription?.let {
                            coinBaseWSService.sendSubscribe(it.copy(type = Subscribe.Type.Unsubscribe.toString()))
                        }
                        latest?.let {
                            coinBaseWSService.sendSubscribe(it)
                            currentSubscription = latest
                        }
                    }
                }
        }

    }
}