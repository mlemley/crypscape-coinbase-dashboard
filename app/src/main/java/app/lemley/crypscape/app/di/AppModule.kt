package app.lemley.crypscape.app.di

import app.lemley.crypscape.client.coinbase.coinbaseApiModule
import app.lemley.crypscape.persistance.persistenceModule
import app.lemley.crypscape.repository.repositoryModule
import app.lemley.crypscape.ui.home.HomeViewModel
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

    factory { DelayedCallback() }
    factory { SyncProductUseCase(get()) }

    viewModel { SplashViewModel(get(), get(), get(named("SplashLoadingMillis"))) }
    viewModel { HomeViewModel() }
}

@FlowPreview
@ExperimentalCoroutinesApi
val appModules = listOf(
    appModule,
    persistenceModule,
    repositoryModule,
    coinbaseApiModule
)

