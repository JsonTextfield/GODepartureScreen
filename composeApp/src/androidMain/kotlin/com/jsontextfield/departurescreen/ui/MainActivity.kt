package com.jsontextfield.departurescreen.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = koinViewModel()
            val uiState by mainViewModel.uiState.collectAsState(MainUIState())
            val isAppearanceLightStatusBars = when (uiState.theme) {
                ThemeMode.LIGHT -> true
                ThemeMode.DARK -> false
                ThemeMode.DEFAULT -> !isSystemInDarkTheme()
            }
            val view = LocalView.current
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    GlanceAppWidgetManager(this@MainActivity).setWidgetPreviews(MyAppWidgetReceiver::class)
                }
            }
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isAppearanceLightStatusBars
            }
            App(mainViewModel)
        }
    }
}