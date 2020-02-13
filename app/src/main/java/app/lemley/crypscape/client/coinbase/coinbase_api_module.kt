package app.lemley.crypscape.client.coinbase

import org.koin.dsl.module

val coinbaseApiModule = module {
    single { CoinBaseApiFactory.coinBaseApiClient() }
}