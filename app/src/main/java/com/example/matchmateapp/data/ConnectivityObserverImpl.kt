package com.example.matchmate.datalayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.matchmate.domain.ConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class ConnectivityObserverImpl @Inject constructor(@ApplicationContext context: Context) : ConnectivityObserver {
  private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

  private val isOnline: Flow<Boolean> = callbackFlow {
    val callback =
      object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          trySend(true)
        }

        override fun onLost(network: Network) {
          trySend(false)
        }
      }
    val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
    connectivityManager.registerNetworkCallback(request, callback)
    awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
  }.distinctUntilChanged()

  override val isBackOnline: Flow<Unit> = flow {
    var wasOffline = false
    isOnline.collect { online ->
      if (online && wasOffline) emit(Unit)
      wasOffline = !online
    }
  }
}
