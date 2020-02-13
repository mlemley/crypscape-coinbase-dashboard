package app.lemley.crypscape.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.lemley.crypscape.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by inject()

    val stateObserver: Observer<HomeViewModel.HomeState> = Observer { state ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.state.observe(viewLifecycleOwner, stateObserver)
        return root
    }
}