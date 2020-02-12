package app.lemley.crypscape.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    private fun createFragmentScenario(
        homeViewModel: HomeViewModel = mockk(relaxUnitFun = true)
    ): FragmentScenario<HomeFragment> {
        val factory: FragmentFactory = object : FragmentFactory() {

            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return HomeFragment(homeViewModel)
            }
        }

        return FragmentScenario.launchInContainer(HomeFragment::class.java, Bundle(), factory)
    }

    @Test
    fun observes_state() {
        val liveDataState: LiveData<HomeViewModel.HomeState> = mockk(relaxUnitFun = true)
        val homeViewModel = mockk<HomeViewModel> {
            every { state } returns liveDataState
        }

        createFragmentScenario(homeViewModel).onFragment { fragment ->
            verify {
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
            }
        }


    }


}