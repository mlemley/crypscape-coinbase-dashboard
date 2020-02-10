package app.lemley.crypscape.usecase

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class DelayedCallbackTest {

    private fun createUseCase():DelayedCallback = DelayedCallback()
    @Test
    fun handles_action() {
        assertThat(createUseCase().canProcess(DelayedCallback.DelayFor(1_000))).isTrue()
    }

    @Test
    fun handles_action__delay_result_passed_back() {
        var actualResult: DelayedCallback.DelayCompletedResult? = null
        runBlocking {
            createUseCase().handleAction(DelayedCallback.DelayFor(1_000)).collect { result ->
                actualResult = result as DelayedCallback.DelayCompletedResult
            }
        }

        assertThat(actualResult).isEqualTo(DelayedCallback.DelayCompletedResult)
    }
}