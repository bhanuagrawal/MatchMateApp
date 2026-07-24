package com.example.matchmateapp.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchmate.domain.MatchRepository
import com.example.matchmateapp.domain.Match
import com.example.matchmateapp.ui.SideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchesViewmodel
@Inject constructor(private val repository: MatchRepository): ViewModel() {

    private val _sideEffects = MutableSharedFlow<SideEffect>(replay = 0, extraBufferCapacity = 1)
    val uiState: StateFlow<MatchesUiState> =
        repository.matches
            .scan(MatchesUiState()){ prev, newList ->
                prev.copy(data = newList)
            }
            .catch { emit(MatchesUiState(isError = true)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchesUiState())

}
sealed class MatchScreenSideEffect(): SideEffect
data class ShowError(val message: String) : MatchScreenSideEffect()