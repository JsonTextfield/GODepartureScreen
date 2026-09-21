package com.jsontextfield.departurescreen.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.util.Consumer
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import io.ktor.http.URLProtocol
import io.ktor.http.buildUrl
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lifecycleScope.launch(Dispatchers.IO) {
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
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    handleIntent(intent)
                    setIntent(intent)
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            LaunchedEffect(isAppearanceLightStatusBars) {
                enableEdgeToEdge(
                    statusBarStyle = if (isAppearanceLightStatusBars) {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    }
                )
            }
            App(mainViewModel)
        }
    }
    private fun handleIntent(intent: Intent) {
        val selectedStop = intent.getStringExtra("selectedStop")
        val stopCode = intent.getStringExtra("stopCode")
        val tripId = intent.getStringExtra("tripId")
        val lineCode = intent.getStringExtra("lineCode")
        val destination = intent.getStringExtra("destination")

        tripId?.let {
            val data = buildMap {
                put("tripId", tripId)
                selectedStop?.let { put("stopName", it) }
                stopCode?.let { put("stopCode", it) }
                lineCode?.let { put("lineCode", it) }
                destination?.let { put("destination", it) }
            }

            val url = buildUrl {
                protocol = URLProtocol.HTTPS
                host = TRIPS_URL
                data.forEach { (key, value) ->
                    encodedParameters.append(
                        key.encodeURLParameter(),
                        value.encodeURLParameter(spaceToPlus = false)
                    )
                }
            }
            DeepLinkHolder.handle(url.toString())
        } ?: selectedStop?.let {
            DeepLinkHolder.handle("$BASE_URL/?selectedStop=$selectedStop")
        }
    }
}