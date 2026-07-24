package com.example.matchmateapp.ui.matches

import com.example.matchmateapp.common.Event
import com.example.matchmateapp.domain.Decision

sealed class MatchesEvent : Event {
    data object Load : MatchesEvent()

    data object LoadMore : MatchesEvent()

    data object Refresh : MatchesEvent()

    data class Decide(val id: String, val decision: Decision) : MatchesEvent()
}
