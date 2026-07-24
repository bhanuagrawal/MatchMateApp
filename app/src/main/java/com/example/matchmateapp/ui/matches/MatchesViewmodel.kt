package com.example.matchmateapp.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchmate.domain.MatchRepository
import com.example.matchmateapp.common.EventResult
import com.example.matchmateapp.common.SideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MatchesViewmodel
@Inject
constructor(
    private val repository: MatchRepository,
    private val loadEventHandler: MatchesLoadEventHandler,
    private val loadMoreEventHandler: MatchesLoadMoreEventHandler,
    private val refreshEventHandler: MatchesRefreshEventHandler,
) : ViewModel() {

    private val events = MutableSharedFlow<MatchesEvent>(extraBufferCapacity = 1)
    private val _sideEffects = MutableSharedFlow<SideEffect>(replay = 0, extraBufferCapacity = 1)
    val sideEffects: SharedFlow<SideEffect> = _sideEffects

    private val initialState = MatchesUiState(isLoading = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MatchesUiState> =
        events
            .onStart { emit(MatchesEvent.Load) }
            .flatMapMerge { handleEvent(it) }
            .onEach { result ->
                if (result is EventResult.Effect) {
                    _sideEffects.emit(result.sideEffect)
                }
            }
            .filterIsInstance<EventResult.UpdateState<MatchesUiState>>()
            .scan(initialState) { state, result -> result.transform(state) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    private fun handleEvent(event: MatchesEvent): Flow<EventResult<MatchesUiState, SideEffect>> =
        when (event) {
            is MatchesEvent.Load -> loadEventHandler.handleEvent(event) { uiState.value }

            is MatchesEvent.LoadMore -> loadMoreEventHandler.handleEvent(event) { uiState.value }

            is MatchesEvent.Refresh -> refreshEventHandler.handleEvent(event) { uiState.value }

            is MatchesEvent.Decide ->
                flow {
                    repository.updateDecision(event.id, event.decision)
                }
        }

    fun onEvent(event: MatchesEvent) {
        viewModelScope.launch { events.emit(event) }
    }
}

sealed class MatchScreenSideEffect : SideEffect {
    data class ShowError(val message: String) : MatchScreenSideEffect()
}
