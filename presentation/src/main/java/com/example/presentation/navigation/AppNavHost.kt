package com.example.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.presentation.feature.auth.AuthRoute
import com.example.presentation.feature.splash.SplashRoute
import com.example.presentation.manager.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppNavHost(
    modifier: Modifier,
    authState: AuthState,
    navController: NavHostController
) {
    LaunchedEffect(authState) {
        withContext(Dispatchers.Main) {
            when (authState) {
                is AuthState.Loading -> {}

                is AuthState.Authenticated -> {
                    navController.navigate(Graphs.Home) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is AuthState.NeedsAuth -> {
                    navController.navigate(Graphs.Auth) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    NavHost(
        modifier = modifier
            .fillMaxSize(),
        navController = navController,
        startDestination = Graphs.Splash
    ) {
        composable<Graphs.Splash> {
            SplashRoute()
        }
        composable<Graphs.Auth> {
            AuthRoute()
        }
        homeGraph(navController)
    }
}

private fun NavGraphBuilder.homeGraph(
    navController: NavController
) {
    navigation<Graphs.Home>(
        startDestination = Home.Notes
    ) {
        composable<Home.Notes> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Notes")
            }
        }

        composable<Home.Crypto> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Crypto")
            }
        }

        composable<Home.CryptoDetails> {}
    }
}