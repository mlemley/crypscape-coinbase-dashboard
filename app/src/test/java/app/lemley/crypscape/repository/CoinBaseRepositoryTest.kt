package app.lemley.crypscape.repository

import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Test

class CoinBaseRepositoryTest {

    private fun createRepository(
        coinBaseCurrencyRepository: CoinBaseCurrencyRepository = mockk(relaxUnitFun = true),
        coinBaseProductRepository: CoinBaseProductRepository = mockk(relaxUnitFun = true)

    ): CoinBaseRepository = CoinBaseRepository(
        currencyRepository = coinBaseCurrencyRepository,
        productRepository = coinBaseProductRepository
    )

    @Test
    fun syncs_products_sync___both_currencies_and_products() {
        val currencyRepository: CoinBaseCurrencyRepository = mockk(relaxUnitFun = true)
        val productRepository: CoinBaseProductRepository = mockk(relaxUnitFun = true)
        val repository = createRepository(currencyRepository, productRepository)

        repository.syncProducts()

        verifyOrder {
            currencyRepository.sync()
            productRepository.sync()
        }

        confirmVerified(currencyRepository, productRepository)
    }


}