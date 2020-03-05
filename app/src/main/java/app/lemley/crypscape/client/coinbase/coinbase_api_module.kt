package app.lemley.crypscape.client.coinbase

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coinbaseApiModule = module {
    single { CoinBaseApiFactory.coinbaseApi() }
    single { CoinBaseApiFactory.coinBaseWSClient(androidApplication()) }
}