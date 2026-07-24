package com.example.matchmate.domain

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
  val isBackOnline: Flow<Unit>
}
