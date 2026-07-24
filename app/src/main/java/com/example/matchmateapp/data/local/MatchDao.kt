package com.example.matchmate.datalayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
  @Query("SELECT * FROM matches ORDER BY page, name")
  fun observeAll(): Flow<List<MatchEntity>>

  @Query("SELECT COUNT(*) FROM matches")
  suspend fun count(): Int

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(matches: List<MatchEntity>)

  @Query("UPDATE matches SET decision = :decision WHERE id = :id")
  suspend fun updateDecision(id: String, decision: String)
}
