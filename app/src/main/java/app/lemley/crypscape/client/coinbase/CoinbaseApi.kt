package app.lemley.crypscape.client.coinbase

import app.lemley.crypscape.client.coinbase.model.Currency
import app.lemley.crypscape.client.coinbase.model.Product
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.TimeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface CoinbaseApi {
    @GET("/time/")
    fun time(): TimeResponse

    @GET("/products/")
    fun products(): List<Product>

    @GET("/currencies/")
    fun currencies(): List<Currency>

    @GET("/products/{remote_id}/")
    fun getProductTicker(@Path("remote_id") remoteId: String): Ticker

    @GET("/products/{remote_id}/candles/")
    fun getCandles(@Path("remote_id") remoteId: String, @QueryMap options: Map<String, String>): Array<Array<Double>>
}