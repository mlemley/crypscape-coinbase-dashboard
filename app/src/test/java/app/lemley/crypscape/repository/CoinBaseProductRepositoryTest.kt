package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.model.CoinBaseApiClient
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import app.lemley.crypscape.repository.converter.ProductConverter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import app.lemley.crypscape.client.coinbase.model.Product as CBProduct

class CoinBaseProductRepositoryTest {


    private fun createRepository(
        coinBaseApiClient: CoinBaseApiClient = mockk(relaxed = true),
        productDao: ProductDao = mockk(relaxed = true),
        currencyDao: CurrencyDao = mockk(relaxed = true),
        platformDao: PlatformDao = mockk(relaxed = true)

    ): CoinBaseProductRepository = CoinBaseProductRepository(
        coinBaseApiClient,
        ProductConverter(),
        productDao,
        currencyDao,
        platformDao
    )

    @Test
    fun syncing_currency_will_persist_data_in_storage() = runBlockingTest {
        val coinBaseApiClient: CoinBaseApiClient = mockk(relaxed = true) {
            every { runBlocking { products() } } returns listOf(
                CBProduct(
                    id = "BTC-USD",
                    baseCurrency = "BTC",
                    quoteCurrency = "USD",
                    quoteIncrement = 0.01,
                    baseMaxSize = 10_000.00,
                    baseMinSize = 0.01
                ),
                CBProduct(
                    id = "BTC-GBP",
                    baseCurrency = "BTC",
                    quoteCurrency = "GBP",
                    quoteIncrement = 0.01,
                    baseMaxSize = 10_000.00,
                    baseMinSize = 0.01
                ),
                CBProduct(
                    id = "ETH-GBP",
                    baseCurrency = "ETH",
                    quoteCurrency = "GBP",
                    quoteIncrement = 0.01,
                    baseMaxSize = 10_000.00,
                    baseMinSize = 0.01
                )
            )
        }
        val expectedConversions = listOf(
            Product(
                platformId = 1,
                serverId = "BTC-USD",
                baseCurrency = 1,
                quoteCurrency = 2,
                quoteIncrement = 0.01,
                baseMaxSize = 10_000.00,
                baseMinSize = 0.01
            ),
            Product(
                platformId = 1,
                serverId = "BTC-GBP",
                baseCurrency = 1,
                quoteCurrency = 3,
                quoteIncrement = 0.01,
                baseMaxSize = 10_000.00,
                baseMinSize = 0.01
            )
        )
        val currencyDao: CurrencyDao = mockk(relaxed = true) {
            every { currencyBy(1, "BTC") } returns mockk { every { id } returns 1 }
            every { currencyBy(1, "USD") } returns mockk { every { id } returns 2 }
            every { currencyBy(1, "GBP") } returns mockk { every { id } returns 3 }
            every { currencyBy(1, "ETH") } returns null
        }
        val productDao: ProductDao = mockk(relaxed = true)
        val platform: Platform = mockk() {
            every { id } returns 1
        }
        val platformDao: PlatformDao = mockk(relaxed = true) {
            every { coinbasePro } returns platform
        }
        val repository = createRepository(coinBaseApiClient, productDao, currencyDao, platformDao)

        repository.sync()

        verifyOrder {
            productDao.insertOrUpdate(expectedConversions[0])
            productDao.insertOrUpdate(expectedConversions[1])
        }

        confirmVerified(productDao)
    }


}