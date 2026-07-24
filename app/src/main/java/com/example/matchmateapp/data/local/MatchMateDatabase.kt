package com.example.matchmate.datalayer.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MatchEntity::class, DecisionEntity::class], version = 1)
abstract class MatchMateDatabase : RoomDatabase() {
  abstract fun matchDao(): MatchDao

  abstract fun decisionDao(): DecisionDao
}
