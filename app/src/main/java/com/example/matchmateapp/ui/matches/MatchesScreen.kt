package com.example.matchmateapp.ui.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import com.example.matchmateapp.navigation.Destination
import com.example.matchmateapp.ui.theme.MatchMateAppTheme


@Composable
fun MatchesScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchesViewmodel = hiltViewModel(),
    navigate: (Destination) -> Unit,
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            if (effect is MatchScreenSideEffect.ShowError) {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        when {
            state.isLoading ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            state.isError -> Text("Error loading matches: ${state.error?.message}")

            else ->
                MatchesScreen(
                    matches = state.data,
                    reachedEnd = state.reachedEnd,
                    isRefreshing = state.isRefreshing,
                    onAccept = { viewModel.onEvent(MatchesEvent.Decide(it, Decision.ACCEPTED)) },
                    onDecline = { viewModel.onEvent(MatchesEvent.Decide(it, Decision.DECLINED)) },
                    onLoadMore = { viewModel.onEvent(MatchesEvent.LoadMore) },
                    onRefresh = { viewModel.onEvent(MatchesEvent.Refresh) },
                    modifier = Modifier.padding(innerPadding),
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MatchesScreen(
    matches: List<Match>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit = {},
    reachedEnd: Boolean = false,
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val currentMatches by rememberUpdatedState(matches)
    val currentReachedEnd by rememberUpdatedState(reachedEnd)
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= currentMatches.size - 3 && currentMatches.isNotEmpty() && !currentReachedEnd
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) onLoadMore() }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh, modifier = modifier) {
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text(text = "Profile Matches", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(matches, key = { it.id }) { match ->
                MatchCard(
                    match = match,
                    onAccept = { onAccept(match.id) },
                    onDecline = { onDecline(match.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (reachedEnd && matches.isNotEmpty()) {
                item {
                    Text(
                        text = "You've seen everyone for now",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchesScreenPreview() {
    MatchMateAppTheme {
        MatchesScreen(
            matches =
                listOf(
                    Match("1", "Adilson Pultrum", 56, "Oudega gem, Drenthe", "", Decision.NONE),
                    Match("2", "Florence Gagné", 43, "Keswick, Yukon", "", Decision.ACCEPTED),
                ),
            onAccept = {},
            onDecline = {},
            onLoadMore = {},
        )
    }
}
