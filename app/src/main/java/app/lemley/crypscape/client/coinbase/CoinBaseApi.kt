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
    suspend fun time(): TimeResponse

    @GET("/products/")
    suspend fun products(): List<Product>

    @GET("/currencies/")
    suspend fun currencies(): List<Currency>

    @GET("/products/{remote_id}/ticker")
    suspend fun tickerFor(
        @Path("remote_id") remoteId: String
    ): Ticker

    @GET("/products/{remote_id}/candles/")
    suspend fun candlesFor(
        @Path("remote_id") remoteId: String,
        @QueryMap options: Map<String, String>
    ): Array<Array<Double>>
}