package app.lemley.crypscape.client.coinbase.model

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.client.coinbase.CoinBaseApiFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class CoinBaseApiClientTest {

    private val mockWebServer = MockWebServer()

    private fun createClient(): CoinBaseApiClient {
        return CoinBaseApiFactory.coinBaseApiClient(baseUrl = mockWebServer.url("/").toString())
    }

    @Before
    fun setUp() {
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetches_ticker_price() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.ticker)
        val product = Product(id = "BTC-USD")

        val ticker = createClient().tickerFor(product)
        val recordedRequest = mockWebServer.takeRequest()

        assertThat(recordedRequest.path).isEqualTo("/products/BTC-USD/")
        assertThat(ticker).isEqualTo(
            Ticker(
                tradeId = 4729088,
                price = 333.99,
                size = 0.193,
                bid = 333.98,
                ask = 333.99,
                volume = 5957.11914015,
                time = "2015-11-14T20:46:03.511254Z"
            )
        )
    }

    @Test
    fun fetches_time() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.time)

        val timeResponse = createClient().time()
        val recordedRequest = mockWebServer.takeRequest()

        assertThat(recordedRequest.path).isEqualTo("/time/")
        assertThat(timeResponse?.iso).isEqualTo("2015-01-07T23:47:25.201Z")
        assertThat(timeResponse?.epochAsMillis).isEqualTo(1420674445201)
    }

    @Test
    fun fetches_currencies() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.currencies)
        val expected = listOf<Currency>(
            Currency(id = "BTC", name = "Bitcoin", minSize = 0.00000001),
            Currency(id = "USD", name = "United States Dollar", minSize = 0.01000000)
        )

        val currencies = createClient().currencies()
        val recordedRequest = mockWebServer.takeRequest()

        assertThat(recordedRequest.path).isEqualTo("/currencies/")
        assertThat(currencies).isEqualTo(expected)
    }

    @Test
    fun fetches_products() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.products)
        val expected = listOf(
            Product(
                id = "BTC-USD",
                baseCurrency = "BTC",
                quoteCurrency = "USD",
                baseMaxSize = 10_000.00,
                baseMinSize = 0.001,
                quoteIncrement = 0.01
            )
        )

        val currencies = createClient().products()
        val recordedRequest = mockWebServer.takeRequest()

        assertThat(recordedRequest.path).isEqualTo("/products/")
        assertThat(currencies).isEqualTo(expected)
    }

    @Test
    fun fetches_candles_for_products() = runBlocking {
        val product = Product(id = "BTC-USD")
        val candleRequest = CandleRequest(
            product,
            granularity = Granularity.FiveMinutes,
            start = "2018-12-26T12:20:00.000Z",
            end = "2018-12-26T17:20:00.000Z"

        )
        val expected = arrayOf(
            arrayOf(1415398768.0, 0.32, 4.2, 0.35, 4.2, 12.3),
            arrayOf(1415398767.0, 0.31, 4.1, 0.34, 4.1, 12.2)
        )
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.candles)

        val currencies = createClient().candlesFor(candleRequest)
        val recordedRequest = mockWebServer.takeRequest()

        assertThat(recordedRequest.path).isEqualTo("/products/BTC-USD/candles/?granularity=300&start=2018-12-26T12%3A20%3A00.000Z&end=2018-12-26T17%3A20%3A00.000Z")
        assertThat(currencies).isEqualTo(expected)
    }
}