package app.lemley.crypscape.ui.market

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers.loadModules
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MarketFragmentTest {

    private fun createFragmentScenario(
        liveDataState: LiveData<MarketState> = mockk(relaxUnitFun = true),
        marketViewModel: MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
        }
    ): FragmentScenario<MarketFragment> {
        loadModules(module {
            viewModel { marketViewModel }
        })

        return FragmentScenario.launchInContainer(MarketFragment::class.java)
    }

    @Test
    fun observes_state__broadcasts_init_event() {
        val liveDataState: LiveData<MarketState> = mockk(relaxUnitFun = true)
        val marketViewModel:MarketViewModel = mockk(relaxUnitFun = true) {
            every { state } returns liveDataState
        }

        excludeRecords {
            marketViewModel.state
        }

        createFragmentScenario(marketViewModel= marketViewModel).onFragment { fragment ->
            verifyOrder {
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
                marketViewModel.dispatchEvent(MarketEvents.Init)
            }
        }

        confirmVerified(liveDataState, marketViewModel)
    }


}