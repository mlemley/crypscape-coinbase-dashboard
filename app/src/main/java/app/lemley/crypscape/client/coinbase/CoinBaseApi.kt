package app.lemley.crypscape.client.coinbase

import app.lemley.crypscape.client.coinbase.model.Currency
import app.lemley.crypscape.client.coinbase.model.Product
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.TimeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface CoinBaseApi {
    @GET("/time/")
    fun timeAsync(): Deferred<Response<TimeResponse>>

    @GET("/products/")
    fun productsAsync(): Deferred<Response<List<Product>>>

    @GET("/currencies/")
    fun currenciesAsync(): Deferred<Response<List<Currency>>>

    @GET("/products/{remote_id}/ticker")
    fun tickerForAsync(
        @Path("remote_id") remoteId: String
    ): Deferred<Response<Ticker>>

    @GET("/products/{remote_id}/candles/")
    fun candlesForAsync(
        @Path("remote_id") remoteId: String,
        @QueryMap options: Map<String, String>
    ): Deferred<Response<Array<Array<Double>>>>
}