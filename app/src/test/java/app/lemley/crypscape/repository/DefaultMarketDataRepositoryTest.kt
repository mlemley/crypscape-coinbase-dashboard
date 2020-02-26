package app.lemley.crypscape.repository

import android.content.SharedPreferences
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DefaultMarketDataRepositoryTest {

    private fun createRepository(
        sharedPreferences: SharedPreferences = mockk(relaxUnitFun = true),
        platformDao: PlatformDao = mockk(relaxUnitFun = true),
        productDao: ProductDao = mockk(relaxUnitFun = true)
    ): DefaultMarketDataRepository = DefaultMarketDataRepository(
        sharedPreferences,
        platformDao,
        productDao
    )

    @Test
    fun creates_a_default_configuration() {
        val coinbasePlatform: Platform = mockk {
            every { id } returns 1
        }

        val defaultProduct: Product = mockk {
            every { serverId } returns "BTC-USD"
        }

        val platformDao: PlatformDao = mockk {
            every { coinbasePro } returns coinbasePlatform
        }

        val productDao: ProductDao = mockk {
            every { byServerId(1, "BTC-USD") } returns defaultProduct
        }

        val repository = createRepository(platformDao = platformDao, productDao = productDao)

        runBlocking {
            assertThat(repository.createDefault()).isEqualTo(
                MarketConfiguration(
                    1L,
                    "BTC-USD",
                    Granularity.Hour
                )
            )
        }
    }

    @Test
    fun loads_default___creates_and_saves__when_default_not_defined() {
        val platform = Platform(id = 1)
        val product = Product(
            platformId = 1,
            id = 2,
            serverId = "BTC-USD",
            quoteCurrency = 3,
            baseCurrency = 4
        )
        val marketConfiguration = MarketConfiguration(1L, "BTC-USD", Granularity.Hour)

        val platformDao: PlatformDao = mockk {
            every { coinbasePro } returns platform
        }

        val productDao: ProductDao = mockk {
            every { byServerId(1, "BTC-USD") } returns product
        }


        val editor: SharedPreferences.Editor = mockk(relaxUnitFun = true) {
            every { putString(marketConfiguration.toJson(), null) } returns this
        }

        val sharedPreferences: SharedPreferences = mockk(relaxUnitFun = true) {
            every { contains(DefaultMarketDataRepository.preferenceKey) } returns false
            every { edit() } returns editor
        }

        val repository = createRepository(
            sharedPreferences = sharedPreferences,
            productDao = productDao,
            platformDao = platformDao
        )

        runBlocking {
            val default = repository.loadDefault()
            assertThat(default).isEqualTo(marketConfiguration)
        }

        verify {
            editor.putString(marketConfiguration.toJson(), null)
            editor.apply()
        }
    }

    @Test
    fun loads_default___returns_default_from_shared_preferences() {
        val serializedData = """
           {"platformId":1, "productRemoteId":"BTC-USD", "granularity":"Hour"}
        """.trimIndent()
        val marketConfiguration = MarketConfiguration(1L, "BTC-USD", Granularity.Hour)
        val sharedPreferences: SharedPreferences = mockk {
            every {
                runBlocking {
                    getString(
                        DefaultMarketDataRepository.preferenceKey,
                        null
                    )
                }
            } returns serializedData
            every { contains(DefaultMarketDataRepository.preferenceKey) } returns true
        }

        val repository = createRepository(sharedPreferences = sharedPreferences)

        runBlocking {
            assertThat(repository.loadDefault()).isEqualTo(marketConfiguration)
        }
    }

    @Test
    fun changes_default_with_new_granularity() {
        val granularity = Granularity.Minute
        val serializedData = """
           {"platformId":1, "productRemoteId":"BTC-USD", "granularity":"Hour"}
        """.trimIndent()
        val marketConfiguration = MarketConfiguration(1L, "BTC-USD", Granularity.Hour)

        val editor: SharedPreferences.Editor = mockk(relaxUnitFun = true) {
            every { putString(marketConfiguration.toJson(), null) } returns this
            every {
                putString(
                    marketConfiguration.copy(granularity = granularity).toJson(),
                    null
                )
            } returns this
        }

        val sharedPreferences: SharedPreferences = mockk {
            every { edit() } returns editor
            every {
                runBlocking {
                    getString(
                        DefaultMarketDataRepository.preferenceKey,
                        null
                    )
                }
            } returns serializedData
            every { contains(DefaultMarketDataRepository.preferenceKey) } returns true
        }

        val repository = createRepository(sharedPreferences = sharedPreferences)

        assertThat(repository.changeGranularity(granularity)).isEqualTo(
            marketConfiguration.copy(
                granularity = granularity
            )
        )

        verifyOrder {
            editor.putString(marketConfiguration.copy(granularity = granularity).toJson(), null)
            editor.apply()
        }
    }
}