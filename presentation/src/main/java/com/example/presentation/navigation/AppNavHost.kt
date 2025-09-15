package com.example.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.presentation.MainVM
import com.example.presentation.common.utils.AuthState
import com.example.presentation.feature.auth.AuthRoute
import com.example.presentation.feature.home.crypto.CryptoRoute
import com.example.presentation.feature.home.crypto.coinDetails.CoinDetailsRoute
import com.example.presentation.feature.home.notes.NotesRoute
import com.example.presentation.feature.splash.SplashRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppNavHost(
    modifier: Modifier,
    authState: AuthState,
    navController: NavHostController,
    viewModel: MainVM
) {
    val lastAuthenticatedRoute by viewModel.lastAuthenticatedRoute.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        withContext(Dispatchers.Main) {
            when (authState) {
                is AuthState.Loading -> {}

                is AuthState.Authenticated -> {
                    val targetRoute = if (lastAuthenticatedRoute != null) viewModel.getRouteAfterAuth()
                    else Graphs.Home

                    navController.navigate(targetRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }

                    viewModel.clearLastRoute()
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
        composable<Graphs.Auth> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Auth>()
            }
            AuthRoute(hiltViewModel(parentEntry))
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
        composable<Home.Notes> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Home>()
            }
            NotesRoute(hiltViewModel(parentEntry))
        }

        composable<Home.Crypto> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Home>()
            }
            CryptoRoute(hiltViewModel(parentEntry)){ coinId ->
                navController.navigate(Home.CoinDetails(coinId))
            }
        }

        composable<Home.CoinDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Home.CoinDetails>()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Home.CoinDetails>()
            }

            CoinDetailsRoute(
                viewModel = hiltViewModel(parentEntry),
                coinId = args.coinId
            ) {
                navController.popBackStack()
            }
        }
    }
}