package app.lemley.crypscape.repository.converter

import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import app.lemley.crypscape.client.coinbase.model.Product as CBProduct

class ProductConverterTest {

    @Test
    fun converts_product_from_coinbase_to_persistable_product() {
        val currencyDao: CurrencyDao = mockk(relaxed = true) {
            every { currencyBy(1, "BTC") } returns mockk { every { id } returns 1 }
            every { currencyBy(1, "USD") } returns mockk { every { id } returns 2 }
        }
        val platform: Platform = mockk {
            every { id } returns 1
        }
        val cbProduct = CBProduct(
            id = "BTC-USD",
            quoteCurrency = "USD",
            baseCurrency = "BTC",
            quoteIncrement = 0.01,
            baseMinSize = 0.00000001,
            baseMaxSize = 10_000.0
        )
        val expectedProduct = Product(
            platformId = 1,
            serverId = "BTC-USD",
            quoteCurrency = 2,
            baseCurrency = 1,
            baseMaxSize = 10_000.0,
            baseMinSize = 0.00000001,
            quoteIncrement = 0.01
        )

        assertThat(ProductConverter().convert(platform, currencyDao, cbProduct)).isEqualTo(
            expectedProduct
        )
    }

    @Test
    fun converts_product_from_coinbase_to_persistable_product__null_when_currency_lookup_fails() {
        val currencyDao: CurrencyDao = mockk {
            every { currencyBy(1, "BTC") } returns mockk { every { id } returns 1 }
            every { currencyBy(1, "USD") } returns null
        }
        val platform: Platform = mockk {
            every { id } returns 1
        }

        val cbProduct = CBProduct(
            id = "BTC-USD",
            quoteCurrency = "USD",
            baseCurrency = "BTC",
            quoteIncrement = 0.01,
            baseMinSize = 0.00000001,
            baseMaxSize = 10_000.0
        )
        assertThat(ProductConverter().convert(platform, currencyDao, cbProduct)).isNull()
    }
}