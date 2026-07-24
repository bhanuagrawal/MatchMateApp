package com.example.matchmate.di

import android.content.Context
import androidx.room.Room
import com.example.matchmate.datalayer.local.DecisionDao
import com.example.matchmate.datalayer.local.MatchDao
import com.example.matchmate.datalayer.local.MatchMateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): MatchMateDatabase =
    Room.databaseBuilder(context, MatchMateDatabase::class.java, "matchmate.db")
      .fallbackToDestructiveMigration(true)
      .build()

  @Provides fun provideMatchDao(database: MatchMateDatabase): MatchDao = database.matchDao()

  @Provides fun provideDecisionDao(database: MatchMateDatabase): DecisionDao = database.decisionDao()
}
