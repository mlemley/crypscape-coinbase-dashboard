package app.lemley.crypscape.client.coinbase.model

import app.lemley.crypscape.client.BaseClient
import app.lemley.crypscape.client.coinbase.CoinBaseApi

class CoinBaseApiClient(
    private val api: CoinBaseApi
) : BaseClient() {

    suspend fun time(): TimeResponse? = safeApiCall(
        call = { api.timeAsync().await() },
        errorMessage = "Error fetching time from coinBase"
    )

    suspend fun tickerFor(product: Product): Ticker? = safeApiCall(
        call = { api.tickerForAsync(product.id).await() },
        errorMessage = "Error fetching ticker for ${product.id}"
    )

    suspend fun currencies(): List<Currency>? = safeApiCall(
        call = { api.currenciesAsync().await() },
        errorMessage = "Error fetching currencies from coinBase"
    )

    suspend fun products(): List<Product>? = safeApiCall(
        call = { api.productsAsync().await() },
        errorMessage = "Error fetching products from coinBase"
    )

    suspend fun candlesFor(candleRequest: CandleRequest): Array<Array<Double>>? = safeApiCall(
        call = { api.candlesForAsync(candleRequest.product.id, candleRequest.asMap()).await() },
        errorMessage = "Error fetching products from coinBase"
    )
}