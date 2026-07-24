package com.example.matchmateapp.ui.matches

import com.example.matchmateapp.domain.Match

data class MatchesUiState (
    val isLoading: Boolean =false,
    val isError: Boolean = false,
    val data: List<Match> = emptyList(),
    val error: Throwable? = null,
)