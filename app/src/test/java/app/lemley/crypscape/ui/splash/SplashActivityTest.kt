package app.lemley.crypscape.ui.splash

import androidx.lifecycle.LiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers.loadModules
import app.lemley.crypscape.ui.MainActivity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.Shadows.shadowOf

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    private fun createScenario(
        liveDataState: LiveData<SplashViewModel.SplashState> = mockk(relaxUnitFun = true)
    ): ActivityScenario<SplashActivity> {

        val module = module {
            viewModel {
                mockk<SplashViewModel>(relaxUnitFun = true) {
                    every { state } returns liveDataState
                }
            }
        }
        loadModules(module)
        return ActivityScenario.launch(SplashActivity::class.java)
    }

    @Test
    fun observes_view_model_state() {
        val liveDataState: LiveData<SplashViewModel.SplashState> = mockk(relaxUnitFun = true)
        createScenario(liveDataState).onActivity { activity ->
            verify {
                liveDataState.observe(activity, activity.stateObserver)
            }
        }.close()
    }

    @Test
    fun dispatches_loaded_event() {
        val liveDataState: LiveData<SplashViewModel.SplashState> = mockk(relaxUnitFun = true)
        createScenario(liveDataState).onActivity { activity ->
            verify {
                activity.viewModel.dispatchEvent(SplashViewModel.Events.Init)
            }
        }.close()
    }

    @Test
    fun navigates_home__when_instructed() {
        createScenario().onActivity { activity ->
            activity.stateObserver.onChanged(
                SplashViewModel.SplashState(
                    requiredActions = SplashViewModel.RequiredActions.ProgressForward
                )
            )

            assertThat(
                shadowOf(activity).peekNextStartedActivity().component?.className
            ).isEqualTo(MainActivity::class.java.name)
        }.close()
    }
}