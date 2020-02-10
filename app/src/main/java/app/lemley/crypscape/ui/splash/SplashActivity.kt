package app.lemley.crypscape.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.lemley.crypscape.MainActivity
import app.lemley.crypscape.R
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.ui.splash.SplashViewModel.RequiredActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject


@FlowPreview
@ExperimentalCoroutinesApi
class SplashActivity : AppCompatActivity() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: SplashViewModel by inject()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val stateObserver = Observer<SplashViewModel.SplashState> { state ->
        when (state.requiredActions) {
            is RequiredActions.ProgressForward -> navigateHome()
            else -> {
            }
        }.exhaustive

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        viewModel.state.observe(this, stateObserver)
        viewModel.dispatchEvent(SplashViewModel.Events.Loaded)
    }

    private fun navigateHome() {
        with(Intent(this, MainActivity::class.java)) {
            startActivity(this)
            finish()
        }
    }
}