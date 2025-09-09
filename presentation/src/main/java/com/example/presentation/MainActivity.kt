package com.example.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.common.ui.values.SecureCryptoNotesTheme
import com.example.presentation.navigation.AppNavHost
import com.example.presentation.navigation.AppNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is UiEvent.ExitApp -> finish()
                    is UiEvent.ShowExitMessage -> {
                        Toast.makeText(this@MainActivity,
                            getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
                    }
                    is UiEvent.NavigateBack -> {
                        navController?.popBackStack()
                    }
                }
            }
        }
    }

    private fun setContent(){
        setContent{
            val shouldShowBottomBar by viewModel.shouldShowBottomBar.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            navController = rememberNavController()

            LaunchedEffect(navController) {
                navController!!.addOnDestinationChangedListener { _, destination, _ ->
                    viewModel.onDestinationChanged(destination.route)
                }
            }

            BackHandler{
                viewModel.onBackPressed()
            }

            SecureCryptoNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        contentWindowInsets = WindowInsets(
                            top = 0.dp,
                            bottom = if (shouldShowBottomBar)
                                WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding()
                            else 0.dp
                        ),
                        content = { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                AppNavHost(
                                    modifier = Modifier.fillMaxSize(),
                                    navController = navController!!
                                )

                                val navBackStackEntry by navController!!.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                AnimatedVisibility(
                                    visible = shouldShowBottomBar,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                ) {
                                    AppNavigationBar(currentDestination, navController!!)
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}