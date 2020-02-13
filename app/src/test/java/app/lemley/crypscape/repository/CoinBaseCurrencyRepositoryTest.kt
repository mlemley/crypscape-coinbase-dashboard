package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.model.CoinBaseApiClient
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.repository.converter.CurrencyConverter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import app.lemley.crypscape.client.coinbase.model.Currency as CBCurrency

@ExperimentalCoroutinesApi
class CoinBaseCurrencyRepositoryTest {

    private fun createRepoository(
        coinBaseApiClient: CoinBaseApiClient = mockk(relaxed = true),
        currencyDao: CurrencyDao = mockk(relaxed = true),
        platformDao: PlatformDao = mockk(relaxed = true)

    ): CoinBaseCurrencyRepository = CoinBaseCurrencyRepository(
        coinBaseApiClient,
        CurrencyConverter(),
        currencyDao,
        platformDao
    )

    @Test
    fun syncing_currency_will_persist_data_in_storage() = runBlockingTest {
        val coinBaseApiClient: CoinBaseApiClient = mockk(relaxed = true) {
            every { runBlocking { currencies() } } returns listOf(
                CBCurrency(id = "BTC", name = "Bitcoin", minSize = 0.00000001),
                CBCurrency(id = "USD", name = "United States Dollar", minSize = 0.01)
            )
        }
        val expectedConversions = listOf(
            Currency(serverId = "BTC", name = "Bitcoin", baseMinSize = 0.00000001),
            Currency(serverId = "USD", name = "United States Dollar", baseMinSize = 0.01)
        )
        val currencyDao: CurrencyDao = mockk(relaxed = true)
        val platform:Platform = mockk()
        val platformDao: PlatformDao = mockk(relaxed = true) {
            every { coinbasePro } returns platform
        }
        val repository = createRepoository(coinBaseApiClient, currencyDao, platformDao)

        repository.sync()

        verifyOrder {
            currencyDao.insertOrUpdate(platform, expectedConversions[0])
            currencyDao.insertOrUpdate(platform, expectedConversions[1])
        }

        confirmVerified(currencyDao)


    }


}