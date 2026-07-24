package com.example.matchmateapp.common

sealed class EventResult<out S : ViewState, out E : SideEffect> {
    data class UpdateState<S : ViewState>(val transform: S.() -> S) : EventResult<S, Nothing>()
    data class Effect<E : SideEffect>(val sideEffect: E) : EventResult<Nothing, E>()
}
