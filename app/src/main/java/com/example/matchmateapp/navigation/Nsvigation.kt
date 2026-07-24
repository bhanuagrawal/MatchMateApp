package com.example.matchmateapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matchmateapp.ui.matches.MatchesScreen

@Composable
fun MainNavigation() {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Matches) {

        composable<Matches>{
            MatchesScreen{
                navController.navigate(it)
            }
        }

    }
}
