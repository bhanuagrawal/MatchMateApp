package com.example.matchmate.datalayer.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decisions")
data class DecisionEntity(
  @PrimaryKey val userId: String,
  val decision: String,
  val timestamp: Long,
)
