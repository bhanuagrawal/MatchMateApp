package com.example.matchmate.di

import com.example.matchmate.datalayer.ConnectivityObserverImpl
import com.example.matchmate.datalayer.MatchRepositoryImpl
import com.example.matchmate.domain.ConnectivityObserver
import com.example.matchmate.domain.MatchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds abstract fun bindMatchRepository(impl: MatchRepositoryImpl): MatchRepository

  @Binds
  @Singleton
  abstract fun bindConnectivityObserver(impl: ConnectivityObserverImpl): ConnectivityObserver
}
