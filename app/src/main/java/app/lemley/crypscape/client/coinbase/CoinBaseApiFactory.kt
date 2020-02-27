package app.lemley.crypscape.client.coinbase

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
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

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(coinBaseClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    fun coinBaseApiClient(baseUrl: String = apiBaseUrl): CoinBaseApiClient =
        CoinBaseApiClient(
            retrofit(
                baseUrl
            ).create(CoinBaseApi::class.java)
        )


    fun coinBaseWSClient(application: Application): CoinBaseWSService {
        val scarlet = Scarlet.Builder()
            .webSocketFactory(webSocketOkHttpClient().newWebSocketFactory(wsFeedUrl))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
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