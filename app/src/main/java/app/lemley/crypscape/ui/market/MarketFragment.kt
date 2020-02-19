package app.lemley.crypscape.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.lemley.crypscape.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class MarketFragment : Fragment() {

    private val marketViewModel: MarketViewModel by viewModel()

    val stateObserver: Observer<MarketState> = Observer { state ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        marketViewModel.state.observe(viewLifecycleOwner, stateObserver)
        marketViewModel.dispatchEvent(MarketEvents.Init)
        return root
    }
}