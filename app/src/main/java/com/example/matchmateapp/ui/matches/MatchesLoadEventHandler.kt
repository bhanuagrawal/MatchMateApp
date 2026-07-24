package com.example.matchmateapp.ui.matches

import com.example.matchmate.domain.MatchRepository
import com.example.matchmateapp.common.DispatcherProvider
import com.example.matchmateapp.common.EventHandler
import com.example.matchmateapp.common.EventResult
import com.example.matchmateapp.common.SideEffect
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge

class MatchesLoadEventHandler
@Inject
constructor(
    private val repository: MatchRepository,
    private val dispatcherProvider: DispatcherProvider,
) : EventHandler<MatchesEvent.Load, MatchesUiState> {

    override fun handleEvent(
        event: MatchesEvent.Load,
        stateProvider: () -> MatchesUiState,
    ): Flow<EventResult<MatchesUiState, SideEffect>> {
        val syncAttempted = MutableStateFlow(false)

        return merge<EventResult<MatchesUiState, SideEffect>>(
            combine(repository.matches, syncAttempted) { list, attempted ->
                EventResult.UpdateState<MatchesUiState> {
                    if (list.isEmpty() && !attempted) {
                        copy(isLoading = true)
                    } else {
                        copy(isLoading = false, isError = false, data = list)
                    }
                }
            },
            flow {
                repository.syncCache().onFailure { throwable ->
                    emit(
                        EventResult.Effect(
                            MatchScreenSideEffect.ShowError(throwable.message ?: "Failed to load matches"),
                        ),
                    )
                }
                syncAttempted.value = true
            },
        ).catch { throwable ->
            emit(EventResult.UpdateState<MatchesUiState> { copy(isLoading = false, isError = true, error = throwable) })
        }.flowOn(dispatcherProvider.default)
    }
}
