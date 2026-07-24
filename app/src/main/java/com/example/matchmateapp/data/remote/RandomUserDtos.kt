package com.example.matchmate.datalayer.remote

import kotlinx.serialization.Serializable

@Serializable data class RandomUserResponse(val results: List<UserDto>)

@Serializable
data class UserDto(
  val login: LoginDto,
  val name: NameDto,
  val dob: DobDto,
  val location: LocationDto,
  val picture: PictureDto,
)

@Serializable data class LoginDto(val uuid: String)

@Serializable data class NameDto(val first: String, val last: String)

@Serializable data class DobDto(val age: Int)

@Serializable data class LocationDto(val city: String, val state: String)

@Serializable data class PictureDto(val large: String)
