package app.lemley.crypscape.repository

import app.lemley.crypscape.repository.converter.CandleConverter
import app.lemley.crypscape.repository.converter.CurrencyConverter
import app.lemley.crypscape.repository.converter.ProductConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val repositoryModule = module {
    factory { CurrencyConverter() }
    factory { ProductConverter() }
    factory { CandleConverter() }
    factory { CoinBaseRepository(get(), get(), get(), get()) }
    factory { CoinBaseCurrencyRepository(get(), get(), get(), get()) }
    factory { CoinBaseProductRepository(get(), get(), get(), get(), get()) }
    factory { CoinBaseCandleRepository(get(), get(), get(), get()) }
    factory { CoinBaseTickerRepository(get()) }
}