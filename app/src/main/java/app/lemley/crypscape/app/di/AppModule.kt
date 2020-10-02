package app.lemley.crypscape.app.di

import app.lemley.crypscape.app.AppConfig
import app.lemley.crypscape.app.CoroutineContextProvider
import app.lemley.crypscape.charting.chartingModule
import app.lemley.crypscape.client.coinbase.coinbaseApiModule
import app.lemley.crypscape.extensions.app.sharedPreferences
import app.lemley.crypscape.persistance.persistenceModule
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.repository.repositoryModule
import app.lemley.crypscape.ui.book.DepthChartViewModel
import app.lemley.crypscape.ui.book.OrderBookAdapter
import app.lemley.crypscape.ui.book.OrderBookViewModel
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
    single { CoroutineContextProvider() }

    // Android Services
    single { androidContext().sharedPreferences }

    // Use Cases
    factory { DelayedCallback() }
    factory { SyncProductUseCase(get()) }
    factory { MarketDataUseCase(get(), get()) }

    // View Models
    viewModel { SplashViewModel(get(), get(), AppConfig.SplashLoadingMillis) }
    viewModel { MarketViewModel(get(), get(), get(), get()) }
    viewModel { OrderBookViewModel(get(), get(), get()) }
    viewModel { DepthChartViewModel(get(), get(), get()) }

    // Adapters
    factory { OrderBookAdapter() }

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

