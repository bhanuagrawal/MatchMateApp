package com.example.matchmate.datalayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
  @Query("SELECT * FROM matches ORDER BY sequence")
  fun observeAll(): Flow<List<MatchEntity>>

  @Query("SELECT COUNT(*) FROM matches")
  suspend fun count(): Int

  @Query("SELECT * FROM matches ORDER BY sequence LIMIT :limit")
  suspend fun getFirst(limit: Int): List<MatchEntity>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(matches: List<MatchEntity>)

  @Query("DELETE FROM matches")
  suspend fun deleteAll()
}
