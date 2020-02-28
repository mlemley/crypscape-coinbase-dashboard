package app.lemley.crypscape.app.di

import app.lemley.crypscape.charting.chartingModule
import app.lemley.crypscape.client.coinbase.coinbaseApiModule
import app.lemley.crypscape.extensions.app.sharedPreferences
import app.lemley.crypscape.persistance.persistenceModule
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.repository.repositoryModule
import app.lemley.crypscape.ui.main.mainScreenModule
import app.lemley.crypscape.ui.market.MarketViewModel
import app.lemley.crypscape.ui.splash.SplashViewModel
import app.lemley.crypscape.usecase.DelayedCallback
import app.lemley.crypscape.usecase.MarketDataUseCase
import app.lemley.crypscape.usecase.SyncProductUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {
    // Injectable Constants
    single(named("SplashLoadingMillis")) { 1_000 }

    // Android Services
    single { androidContext().sharedPreferences }


    // Use Cases
    factory { DelayedCallback() }
    factory { SyncProductUseCase(get()) }
    factory { MarketDataUseCase(get(), get()) }

    // View Models
    viewModel { SplashViewModel(get(), get(), get(named("SplashLoadingMillis"))) }
    viewModel { MarketViewModel(get(), get(), get()) }

    // Repositories

    factory { DefaultMarketDataRepository(get(), get(), get()) }
}

@FlowPreview
@ExperimentalCoroutinesApi
val appModules = listOf(
    appModule,
    persistenceModule,
    repositoryModule,
    coinbaseApiModule,
    mainScreenModule,
    chartingModule
)

