package com.example.matchmate.datalayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
  @Query("SELECT * FROM decisions")
  fun observeAll(): Flow<List<DecisionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(decision: DecisionEntity)
}
