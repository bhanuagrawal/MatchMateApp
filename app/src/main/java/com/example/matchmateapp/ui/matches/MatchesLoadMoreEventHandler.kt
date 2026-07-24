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

class MatchesLoadMoreEventHandler
@Inject
constructor(private val repository: MatchRepository) :
    EventHandler<MatchesEvent.LoadMore, MatchesUiState> {

    override fun handleEvent(
        event: MatchesEvent.LoadMore,
        stateProvider: () -> MatchesUiState,
    ): Flow<EventResult<MatchesUiState, SideEffect>> =
        flow {
            if (stateProvider().isLoadingMore || stateProvider().reachedEnd) return@flow

            emit(EventResult.UpdateState<MatchesUiState> { copy(isLoadingMore = true) })

            val result = repository.loadNextPage()

            emit(EventResult.UpdateState<MatchesUiState> { copy(isLoadingMore = false, reachedEnd = result.getOrDefault(reachedEnd)) })

            result.onFailure { throwable ->
                emit(
                    EventResult.Effect(
                        MatchScreenSideEffect.ShowError(throwable.message ?: "Failed to load more matches"),
                    ),
                )
            }
        }.catch { throwable ->
            emit(EventResult.UpdateState<MatchesUiState> { copy(isLoadingMore = false, isError = true, error = throwable) })
        }.flowOn(Dispatchers.IO)
}
