package com.example.matchmate.domain

import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
  /** Local cache only — no network involved, always emits instantly. */
  val matches: Flow<List<Match>>

  suspend fun loadNextPage(): Result<Boolean>

  /**
   * Seeds the cache if empty, otherwise silently reconciles it with the backend
   * (there's no version/etag, so page 1's content is used as the staleness check).
   * Safe to run in the background: callers that already have cached data to show
   * should not gate a loading state on this.
   */
  suspend fun syncCache(): Result<Unit>

  /** Clears the cache and re-fetches from page 1. Returns whether the fetched page was empty. */
  suspend fun refreshMatches(): Result<Boolean>
  suspend fun updateDecision(id: String, decision: Decision)
}
