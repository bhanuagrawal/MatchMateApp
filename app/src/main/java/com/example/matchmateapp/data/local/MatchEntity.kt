package com.example.matchmate.datalayer.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
  @PrimaryKey val id: String,
  val page: Int,
  val name: String,
  val age: Int,
  val location: String,
  val pictureUrl: String,
  val decision: String,
)
