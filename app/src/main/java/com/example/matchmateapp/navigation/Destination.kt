package com.example.matchmateapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination

@Serializable
data object Matches : Destination()
