@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.widget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.jsontextfield.departurescreen.core.ui.navigation.StopsRoute
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import com.jsontextfield.departurescreen.ui.views.StopsScreen
import com.jsontextfield.departurescreen.widget.WidgetSettingsRoute
import com.jsontextfield.departurescreen.widget.ui.DeparturesWidget
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class WidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glanceWidgetManager = GlanceAppWidgetManager(this)
        val glanceId = glanceWidgetManager.getGlanceIdBy(intent)
        val widgetId = glanceId?.let { glanceWidgetManager.getAppWidgetId(glanceId) }

        setContent {
            val view = LocalView.current
            val haptic = LocalHapticFeedback.current
            val widgetNavConfig = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(WidgetSettingsRoute::class, WidgetSettingsRoute.serializer())
                        subclass(StopsRoute::class, StopsRoute.serializer())
                    }
                }
            }
            val backStack = rememberNavBackStack(widgetNavConfig, WidgetSettingsRoute)
            val configViewModel = koinViewModel<WidgetConfigViewModel> {
                parametersOf(widgetId)
            }
            val widgetConfig by configViewModel.config.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            val isAppearanceLightStatusBars = !isSystemInDarkTheme()
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isAppearanceLightStatusBars
            }
            AppTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<WidgetSettingsRoute> {
                            WidgetConfigScreen(
                                widgetConfig = widgetConfig,
                                onSortModeChanged = configViewModel::onSortModeChanged,
                                onTimeFormatChanged = configViewModel::onTimeFormatChanged,
                                onOpacityChanged = {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    configViewModel.onOpacityChanged(it)
                                },
                                onStopButtonClicked = {
                                    backStack.add(StopsRoute(widgetConfig.selectedStopName))
                                },
                                onCancel = {
                                    glanceId?.let {
                                        val resultValue = Intent().putExtra(
                                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                                            glanceWidgetManager.getAppWidgetId(glanceId)
                                        )
                                        setResult(RESULT_CANCELED, resultValue)
                                    }
                                    finish()
                                },
                                onDone = {
                                    widgetId?.let {
                                        configViewModel.saveConfig(widgetId)
                                    }
                                    glanceId?.let {
                                        scope.launch {
                                            DeparturesWidget().update(this@WidgetConfigActivity, glanceId)
                                        }
                                        setResult(
                                            RESULT_OK,
                                            Intent().putExtra(
                                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                glanceWidgetManager.getAppWidgetId(glanceId)
                                            ),
                                        )
                                        finish()
                                    }
                                },
                            )
                        }
                        entry<StopsRoute> { route ->
                            val selectedStopCode = route.selectedStopName
                            val stopsViewModel = koinViewModel<StopsViewModel> {
                                parametersOf(selectedStopCode)
                            }
                            StopsScreen(
                                stopsViewModel = stopsViewModel,
                                onStopSelected = {
                                    configViewModel.onStopChanged(it)
                                    backStack.removeLastOrNull()
                                },
                                onBackPressed = {
                                    backStack.removeLastOrNull()
                                },
                            )
                        }
                    }
                )
            }
        }
    }
}