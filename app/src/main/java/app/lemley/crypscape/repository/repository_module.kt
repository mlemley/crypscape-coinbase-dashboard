package app.lemley.crypscape.repository

import app.lemley.crypscape.repository.converter.CurrencyConverter
import app.lemley.crypscape.repository.converter.ProductConverter
import org.koin.dsl.module

val repositoryModule = module {
    factory { CurrencyConverter() }
    factory { ProductConverter() }
    factory { CoinBaseRepository(get(), get()) }
    factory { CoinBaseCurrencyRepository(get(), get(), get(), get()) }
    factory { CoinBaseProductRepository(get(), get(), get(), get(), get()) }
}