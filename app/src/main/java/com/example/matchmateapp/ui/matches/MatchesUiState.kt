package com.example.matchmateapp.ui.matches

import com.example.matchmateapp.domain.Match

data class MatchesUiState (
    val isLoading: Boolean,
    val isError: Boolean,
    val data: List<Match>,
    val error: Throwable? = null,
)