package com.example.presentation

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
class MainActivity : FragmentActivity() {

    private val viewModel: MainVM by viewModels()
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        setupLifecycleObserver()
        setContent()
        observeUiEvents()
    }

    private fun setupLifecycleObserver() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                viewModel.lockApp()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                viewModel.onAppBackgrounded()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                viewModel.onAppResumed()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                viewModel.onAppResumed()
            }
        })
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
            val authState by viewModel.authState.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }
            navController = rememberNavController()

            SecureCryptoNotesTheme {
                LaunchedEffect(navController) {
                    navController!!.addOnDestinationChangedListener { _, destination, _ ->
                        viewModel.onDestinationChanged(destination.route)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        contentWindowInsets = WindowInsets(0.dp),
                        content = { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                AppNavHost(
                                    modifier = Modifier.fillMaxSize(),
                                    navController = navController!!,
                                    authState = authState,
                                    viewModel = viewModel
                                )

                                val navBackStackEntry by navController!!.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                BackHandler{
                                    viewModel.onBackPressed()
                                }

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