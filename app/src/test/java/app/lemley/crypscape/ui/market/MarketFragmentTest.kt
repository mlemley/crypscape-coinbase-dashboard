package app.lemley.crypscape.ui.market

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers.loadModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        liveDataState: LiveData<MarketViewModel.HomeState> = mockk(relaxUnitFun = true)
    ): FragmentScenario<MarketFragment> {
        val module = module {
            viewModel {
                mockk<MarketViewModel>(relaxUnitFun = true) {
                    every { state } returns liveDataState
                }
            }
        }
        loadModules(module)

        return FragmentScenario.launchInContainer(MarketFragment::class.java)
    }

    @Test
    fun observes_state() {
        val liveDataState: LiveData<MarketViewModel.HomeState> = mockk(relaxUnitFun = true)

        createFragmentScenario(liveDataState).onFragment { fragment ->
            verify {
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
            }
        }


    }


}