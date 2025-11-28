package com.jsontextfield.departurescreen.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val glanceAppWidgetManager = GlanceAppWidgetManager(this)
            lifecycleScope.launch {
                glanceAppWidgetManager.setWidgetPreviews(MyAppWidgetReceiver::class)
            }
        }
        setContent {
            val mainViewModel = koinViewModel<MainViewModel>()
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val isAppearanceLightStatusBars = when (uiState.theme) {
                ThemeMode.LIGHT -> true
                ThemeMode.DARK -> false
                ThemeMode.DEFAULT -> !isSystemInDarkTheme()
            }
            val view = LocalView.current
            LaunchedEffect(Unit) {
                intent.extras?.getString("selectedStation")?.let { stationCode ->
                    // called when launching from a widget for the first time
                    // set selected station
                    mainViewModel.setSelectedStation(stationCode)
                }
            }
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    intent.extras?.getString("selectedStation")?.let { stationCode ->
                        // called when launching from a widget while the activity is already running
                        // set selected station
                        mainViewModel.setSelectedStation(stationCode)
                    }
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isAppearanceLightStatusBars
            }
            App(mainViewModel)
        }
    }
}