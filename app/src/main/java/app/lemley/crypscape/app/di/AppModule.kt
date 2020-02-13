package app.lemley.crypscape.app.di

import app.lemley.crypscape.persistance.persistenceModule
import app.lemley.crypscape.repository.CoinBaseProductRepository
import app.lemley.crypscape.ui.splash.SplashViewModel
import app.lemley.crypscape.usecase.DelayedCallback
import app.lemley.crypscape.usecase.SyncProductUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {
    single(named("SplashLoadingMillis")) { 1_000 }
    viewModel { SplashViewModel(get()) }

    factory { DelayedCallback() }
    factory { SyncProductUseCase(get()) }
    factory { CoinBaseProductRepository(get(), get(), get(), get(), get()) }
}

@FlowPreview
@ExperimentalCoroutinesApi
val appModules = listOf(
    appModule, persistenceModule
)

