package com.example.matchmateapp.domain

enum class Decision { NONE, ACCEPTED, DECLINED }

data class Match(
  val id: String,
  val name: String,
  val age: Int,
  val location: String,
  val pictureUrl: String,
  val decision: Decision,
)
