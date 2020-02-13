package app.lemley.crypscape.app.di

import app.lemley.crypscape.ui.splash.SplashViewModel
import app.lemley.crypscape.usecase.DelayedCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


@FlowPreview
@ExperimentalCoroutinesApi
object AppModule {
    val appModule = module {
        single(named("SplashLoadingMillis")) { 1_000 }
        factory { DelayedCallback() }
        viewModel { SplashViewModel(get()) }
    }
}