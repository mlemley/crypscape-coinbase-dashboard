package app.lemley.crypscape.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.lemley.crypscape.R

class HomeFragment(
    val homeViewModel: HomeViewModel
) : Fragment() {


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