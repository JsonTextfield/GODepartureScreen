@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.widget.config

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsontextfield.departurescreen.core.ui.navigation.StationsRoute
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import com.jsontextfield.departurescreen.ui.views.StationsScreen
import com.jsontextfield.departurescreen.widget.DeparturesWidget
import com.jsontextfield.departurescreen.widget.WidgetSettingsRoute
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class WidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glanceWidgetManager = GlanceAppWidgetManager(this)
        val glanceId = glanceWidgetManager.getGlanceIdBy(intent)
        val widgetId = glanceId?.let { glanceWidgetManager.getAppWidgetId(glanceId) }

        setContent {
            val configViewModel = koinViewModel<WidgetConfigViewModel>()
            val stationsViewModel = koinViewModel<StationsViewModel>()
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val widgetConfig by configViewModel.config.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                widgetId?.let {
                    configViewModel.loadConfig(it)
                }
            }
            LaunchedEffect(widgetConfig) {
                stationsViewModel.loadData(widgetConfig.selectedStationCode)
            }
            AppTheme {
                NavHost(navController, startDestination = WidgetSettingsRoute) {
                    composable<WidgetSettingsRoute> {
                        WidgetConfigScreen(
                            widgetConfig = widgetConfig,
                            onSortModeChanged = configViewModel::onSortModeChanged,
                            onOpacityChanged = configViewModel::onOpacityChanged,
                            onStationButtonClicked = {
                                navController.navigate(StationsRoute)
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