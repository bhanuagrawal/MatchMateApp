package com.example.matchmate.domain

import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
  val matches: Flow<List<Match>>

  suspend fun loadNextPage(): Result<Unit>

  suspend fun updateDecision(id: String, decision: Decision)
}
