package app.lemley.crypscape.client.coinbase

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coinbaseApiModule = module {
    single { CoinBaseApiFactory.coinBaseApiClient() }
    single { CoinBaseApiFactory.coinBaseWSClient(androidApplication()) }
}