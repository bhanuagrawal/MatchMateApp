package com.example.matchmateapp.common

interface ViewState {
    val isLoading: Boolean
    val isError: Boolean
    val error: Throwable?
}
