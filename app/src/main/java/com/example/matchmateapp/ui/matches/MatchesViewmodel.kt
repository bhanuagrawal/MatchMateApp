package com.example.matchmateapp.ui.matches

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MatchesViewmodel: ViewModel() {

    val uiState: StateFlow<MatchesUiState> =
        MutableStateFlow(MatchesUiState(isLoading = true, isError = false, data = emptyList()))
}