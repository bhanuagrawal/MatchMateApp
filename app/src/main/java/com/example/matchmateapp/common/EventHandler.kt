package com.example.matchmateapp.common

import kotlinx.coroutines.flow.Flow

interface EventHandler<E : Event, S : ViewState> {
    fun handleEvent(event: E, stateProvider: () -> S): Flow<EventResult<S, SideEffect>>
}
