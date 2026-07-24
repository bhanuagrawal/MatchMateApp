package com.example.matchmateapp.ui.matches

import com.example.matchmate.domain.MatchRepository
import com.example.matchmateapp.common.EventHandler
import com.example.matchmateapp.common.EventResult
import com.example.matchmateapp.common.SideEffect
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MatchesRefreshEventHandler
@Inject
constructor(private val repository: MatchRepository) :
    EventHandler<MatchesEvent.Refresh, MatchesUiState> {

    override fun handleEvent(
        event: MatchesEvent.Refresh,
        stateProvider: () -> MatchesUiState,
    ): Flow<EventResult<MatchesUiState, SideEffect>> =
        flow {
            if (stateProvider().isRefreshing) return@flow

            emit(EventResult.UpdateState<MatchesUiState> { copy(isRefreshing = true) })

            val result = repository.refreshMatches()

            emit(
                EventResult.UpdateState<MatchesUiState> {
                    copy(isRefreshing = false, isError = false, reachedEnd = result.getOrDefault(reachedEnd))
                },
            )

            result.onFailure { throwable ->
                emit(
                    EventResult.Effect(
                        MatchScreenSideEffect.ShowError(throwable.message ?: "Failed to refresh matches"),
                    ),
                )
            }
        }.catch { throwable ->
            emit(EventResult.UpdateState<MatchesUiState> { copy(isRefreshing = false, isError = true, error = throwable) })
        }.flowOn(Dispatchers.IO)
}
