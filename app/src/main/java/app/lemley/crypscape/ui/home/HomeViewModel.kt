package app.lemley.crypscape.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.BaseViewModel
import app.lemley.crypscape.ui.base.Event
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.ui.base.State
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.flow.Flow

class HomeViewModel : BaseViewModel<HomeViewModel.Events, HomeViewModel.HomeState>() {

    sealed class Events: Event {

    }

    data class HomeState(
        val foo:Boolean = false
    ): State

    override val useCases: List<UseCase>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun makeInitState(): HomeState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Flow<Events>.eventTransform(): Flow<Action> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun HomeState.plus(result: Result): HomeState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}