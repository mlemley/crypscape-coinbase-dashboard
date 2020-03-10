package app.lemley.crypscape.ui.splash

import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.usecase.DelayedCallback
import app.lemley.crypscape.usecase.SyncProductUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class SplashViewModelTest {

    private fun createViewModel(
        syncProductUseCase: SyncProductUseCase = mockk(relaxUnitFun = true),
        delayedCallback: DelayedCallback = mockk(relaxUnitFun = true)

    ): SplashViewModel = SplashViewModel(syncProductUseCase, delayedCallback, 0)

    @Test
    fun eventTransform() {
        val viewModel = createViewModel()
        val events = flowOf(
            SplashViewModel.Events.Init,
            SplashViewModel.Events.ProductsLoaded
        )
        val expected = listOf(
            SyncProductUseCase.SyncProductData,
            DelayedCallback.DelayFor(0)
        )
        val actual = mutableListOf<Action>()
        runBlocking {
            with(viewModel) {
                events.eventTransform().toList(actual)
            }
        }
        assertThat(expected).isEqualTo(actual)
    }

    @Ignore
    @Test
    fun plus__dispatches_products_loaded_event__when_product_loaded_result_occurs() {
        val delayedCallback: DelayedCallback = mockk()
        val viewModel = createViewModel(delayedCallback = delayedCallback)

        runBlocking {

            with(viewModel) {
                makeInitState() + SyncProductUseCase.ProductSyncComplete(mockk(relaxed = true))
            }

        }

        //assertThat(viewModel.events.alTo(listOf(SplashViewModel.Events.ProductsLoaded))
    }

    @Test
    fun plus__progresses_forward_after_delay() {
        val viewModel = createViewModel()
        val initState = viewModel.makeInitState()

        val inputs = listOf(
            DelayedCallback.DelayCompletedResult
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