package app.lemley.crypscape.client.coinbase

import android.app.Application
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.OrderBookDeserializer
import com.google.gson.GsonBuilder
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CoinBaseApiFactory {
    private const val apiBaseUrl = "https://api.pro.coinbase.com"
    private const val wsFeedUrl = "wss://ws-feed.pro.coinbase.com"

    private val coinBaseClient = OkHttpClient().newBuilder()
        // add interceptors here
        .build()

    fun coinbaseApi(baseUrl: String= apiBaseUrl): CoinBaseApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(coinBaseClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(CoinBaseApi::class.java)

    fun coinBaseWSClient(application:Application): CoinBaseWSService {
        val gson = GsonBuilder().registerTypeAdapter(
            OrderBook::class.java,
            OrderBookDeserializer()
        ).create()
        val scarlet = Scarlet.Builder()
            .webSocketFactory(webSocketOkHttpClient().newWebSocketFactory(wsFeedUrl))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory(gson = gson))
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()
        return scarlet.create()
    }

    private fun webSocketOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }
}