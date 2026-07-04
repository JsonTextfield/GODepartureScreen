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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.navigation.TripDetailsRoute
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lifecycleScope.launch {
                val glanceAppWidgetManager = GlanceAppWidgetManager(this@MainActivity)
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
            var isIntentProcessed by rememberSaveable { mutableStateOf(false) }
            var tripDetailsRoute by remember { mutableStateOf<TripDetailsRoute?>(null) }

            fun handleIntent(intent: Intent) {
                val selectedStop = intent.getStringExtra("selectedStop")
                val stopCode = intent.getStringExtra("stopCode")
                val tripId = intent.getStringExtra("tripId")
                val lineCode = intent.getStringExtra("lineCode")
                val destination = intent.getStringExtra("destination")

                if (selectedStop != null && stopCode != null && tripId != null && lineCode != null && destination != null) {
                    tripDetailsRoute = TripDetailsRoute(
                        selectedStop = selectedStop,
                        stopCode = stopCode,
                        tripId = tripId,
                        lineCode = lineCode,
                        destination = destination
                    )
                }
                selectedStop?.let {
                    if (!isIntentProcessed) {
                        mainViewModel.setSelectedStop(it)
                    }
                }
            }

            LaunchedEffect(Unit) {
                handleIntent(intent)
                isIntentProcessed = true
            }
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    handleIntent(intent)
                    setIntent(intent)
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isAppearanceLightStatusBars
            }
            App(mainViewModel, tripDetailsRoute)
        }
    }
}