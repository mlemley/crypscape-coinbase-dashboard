package app.lemley.crypscape.usecase


import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import kotlinx.coroutines.flow.Flow

interface UseCase {
    fun canProcess(action: Action): Boolean
    fun handleAction(action: Action): Flow<Result>
}
