package com.mobileweb3.basic_kmm_sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mobileweb3.basic_kmm_sample.android.screens.main.MainScreen
import com.mobileweb3.basic_kmm_sample.android.ui.AppTheme
import com.mobileweb3.basic_kmm_sample.app.MainSideEffect
import com.mobileweb3.basic_kmm_sample.app.MainStore
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.inject

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            }

            AppTheme {
                ProvideWindowInsets {
                    val scaffoldState = rememberScaffoldState()

                    val store: MainStore by inject()
                    val message = store.observeSideEffect()
                        .filterIsInstance<MainSideEffect.Message>()
                        .collectAsState(MainSideEffect.Message("start message"))

                    LaunchedEffect(message.value) {
                        message.value?.let {
                            scaffoldState.snackbarHostState.showSnackbar(
                                it.text
                            )
                        }
                    }

                    Box(
                        Modifier.padding(
                            rememberInsetsPaddingValues(
                                insets = LocalWindowInsets.current.systemBars,
                                applyStart = true,
                                applyTop = false,
                                applyEnd = true,
                                applyBottom = false
                            )
                        )
                    ) {
                        Scaffold(
                            scaffoldState = scaffoldState,
                            snackbarHost = { hostState ->
                                SnackbarHost(
                                    hostState = hostState,
                                    modifier = Modifier.padding(
                                        rememberInsetsPaddingValues(
                                            insets = LocalWindowInsets.current.systemBars,
                                            applyBottom = true
                                        )
                                    )
                                )
                            }
                        ) {
                            val navController = rememberNavController()

                            NavHost(navController = navController, startDestination = "start") {
                                composable("start") {
                                    MainScreen(
                                        store = store
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
