package app.lemley.crypscape.repository


class CoinBaseRepository(
    val currencyRepository: CoinBaseCurrencyRepository,
    val productRepository: CoinBaseProductRepository
) {

    suspend fun syncProducts() {
        currencyRepository.sync()
        productRepository.sync()
    }

}