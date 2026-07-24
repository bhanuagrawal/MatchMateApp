package com.example.matchmate.domain

import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
  val matches: Flow<List<Match>>

  suspend fun loadNextPage(): Result<Boolean>

  suspend fun syncCache(): Result<Unit>

  suspend fun refreshMatches(): Result<Boolean>
  suspend fun updateDecision(id: String, decision: Decision)
}
