package app.lemley.crypscape.repository


class CoinBaseRepository(
    val currencyRepository: CoinBaseCurrencyRepository,
    val productRepository: CoinBaseProductRepository
) {

    fun syncProducts() {
        currencyRepository.sync()
        productRepository.sync()
    }

}