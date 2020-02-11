package app.lemley.crypscape.client.coinbase.model

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CoinBaseApiFactory {
    const val apiBaseUrl = "https://api.pro.coinbase.com"

    private val coinBaseClient = OkHttpClient().newBuilder()
        // add interceptors here
        .build()

    private fun retrofit(baseUrl:String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(coinBaseClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    fun coinBaseApiClient(baseUrl: String = apiBaseUrl): CoinBaseApiClient =
        CoinBaseApiClient(retrofit(baseUrl).create(CoinBaseApi::class.java))
}