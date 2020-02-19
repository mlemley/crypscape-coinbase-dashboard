package app.lemley.crypscape.ui.market

import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.usecasei.MarketDataUseCase
import app.lemley.crypscape.usecasei.MarketDataUseCase.MarketActions
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModelTest {

    private fun createViewModel(
        marketDataUseCase: MarketDataUseCase = mockk(relaxUnitFun = true)
    ): MarketViewModel = MarketViewModel(
        marketDataUseCase
    )

    @Test
    fun has_expected_use_cases() {
        val marketDataUseCase: MarketDataUseCase = mockk(relaxUnitFun = true)
        val viewModel = createViewModel(marketDataUseCase)

        assertThat(viewModel.useCases).isEqualTo(
            listOf(
                marketDataUseCase
            )
        )
    }

    @Test
    fun maps_events_to_actions() {
        val viewModel = createViewModel()

        val events = flowOf(
            MarketViewModel.Events.Init
        )

        val expectedActions = listOf(
            MarketActions.FetchMarketDataForDefaultConfiguration
        )

        val actual = mutableListOf<Action>()
        with(viewModel) {
            runBlocking {
                events.eventTransform().toList(actual)
            }
        }

        assertThat(actual).isEqualTo(expectedActions)
    }


}