@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.widget.config

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jsontextfield.departurescreen.core.ui.navigation.StationsRoute
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import com.jsontextfield.departurescreen.ui.views.StationsScreen
import com.jsontextfield.departurescreen.widget.WidgetSettingsRoute
import com.jsontextfield.departurescreen.widget.ui.DeparturesWidget
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class WidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glanceWidgetManager = GlanceAppWidgetManager(this)
        val glanceId = glanceWidgetManager.getGlanceIdBy(intent)
        val widgetId = glanceId?.let { glanceWidgetManager.getAppWidgetId(glanceId) }

        setContent {
            val haptic = LocalHapticFeedback.current
            val navController = rememberNavController()
            val configViewModel = koinViewModel<WidgetConfigViewModel> {
                parametersOf(widgetId)
            }
            AppTheme {
                NavHost(navController, startDestination = WidgetSettingsRoute) {
                    composable<WidgetSettingsRoute> {
                        val widgetConfig by configViewModel.config.collectAsStateWithLifecycle()
                        val scope = rememberCoroutineScope()
                        WidgetConfigScreen(
                            widgetConfig = widgetConfig,
                            onSortModeChanged = configViewModel::onSortModeChanged,
                            onOpacityChanged = {
                                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                configViewModel.onOpacityChanged(it)
                            },
                            onStationButtonClicked = {
                                navController.navigate(StationsRoute(widgetConfig.selectedStationCode))
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
                    composable<StationsRoute> {
                        val selectedStationCode = it.toRoute<StationsRoute>().selectedStationCode
                        val stationsViewModel = koinViewModel<StationsViewModel> {
                            parametersOf(selectedStationCode)
                        }
                        StationsScreen(
                            stationsViewModel = stationsViewModel,
                            onStationSelected = {
                                configViewModel.onStationChanged(it)
                                navController.popBackStack()
                            },
                            onBackPressed = {
                                navController.popBackStack()
                            },
                        )
                    }
                }
            }
        }
    }
}