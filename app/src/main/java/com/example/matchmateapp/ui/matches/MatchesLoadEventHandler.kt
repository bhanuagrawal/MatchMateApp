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

class MatchesLoadEventHandler
@Inject
constructor(private val repository: MatchRepository) :
    EventHandler<MatchesEvent.Load, MatchesUiState> {

    override fun handleEvent(
        event: MatchesEvent.Load,
        stateProvider: () -> MatchesUiState,
    ): Flow<EventResult<MatchesUiState, SideEffect>> =
        flow {
            var hasSynced = false

            repository.matches.collect { list ->
                if (!hasSynced && list.isEmpty()) {
                    hasSynced = true

                    repository.syncCache().onFailure { throwable ->
                        emit(
                            EventResult.UpdateState<MatchesUiState> {
                                copy(isLoading = false, isError = true, error = throwable)
                            },
                        )
                        emit(
                            EventResult.Effect(
                                MatchScreenSideEffect.ShowError(throwable.message ?: "Failed to load matches"),
                            ),
                        )
                    }
                    return@collect
                }

                emit(EventResult.UpdateState<MatchesUiState> { copy(isLoading = false, isError = false, data = list) })

                if (!hasSynced) {
                    hasSynced = true
                    repository.syncCache()
                }
            }
        }.catch { throwable ->
            emit(EventResult.UpdateState<MatchesUiState> { copy(isLoading = false, isError = true, error = throwable) })
        }.flowOn(Dispatchers.IO)
}
