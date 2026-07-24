package com.example.matchmateapp.ui.matches

import com.example.matchmateapp.common.ViewState
import com.example.matchmateapp.domain.Match

data class MatchesUiState(
    override val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val reachedEnd: Boolean = false,
    override val isError: Boolean = false,
    val data: List<Match> = emptyList(),
    override val error: Throwable? = null,
) : ViewState