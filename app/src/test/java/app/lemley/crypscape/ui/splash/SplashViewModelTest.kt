package app.lemley.crypscape.ui.splash

import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.usecase.DelayedCallback
import app.lemley.crypscape.usecase.SyncProductUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class SplashViewModelTest {

    private fun createViewModel(
        syncProductUseCase: SyncProductUseCase = mockk(relaxUnitFun = true)

    ): SplashViewModel = SplashViewModel(syncProductUseCase)

    @Test
    fun eventTransform() {
        val viewModel = createViewModel()
        val events = flowOf(
            SplashViewModel.Events.Loaded
        )
        val expected = listOf(
            SyncProductUseCase.SyncProductData
        )
        val actual = mutableListOf<Action>()
        runBlocking {
            with(viewModel) {
                events.eventTransform().toList(actual)
            }
        }
        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun plus() {
        val viewModel = createViewModel()
        val initState = viewModel.makeInitState()

        val inputs = listOf(
            SyncProductUseCase.ProductSyncComplete
        )
        val expected = listOf(
            initState.copy(requiredActions = SplashViewModel.RequiredActions.ProgressForward)
        )
        with(viewModel) {
            val actual = inputs.map { initState + it }
            assertThat(expected).isEqualTo(actual)
        }
    }
}