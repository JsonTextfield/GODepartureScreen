@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.getFullSubject
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.theme.darkScheme
import com.jsontextfield.departurescreen.core.ui.theme.lightScheme
import com.jsontextfield.departurescreen.ui.MainActivity
import com.jsontextfield.departurescreen.widget.ui.components.RefreshButton
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AlertsWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        sizes = setOf(
            DpSize(210.dp, 120.dp),
            DpSize(480.dp, 240.dp),
            DpSize(640.dp, 480.dp),
            DpSize(1000.dp, 640.dp),
        )
    )

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val uiState = AlertsWidgetUIState(
            status = Status.LOADED,
            _alerts = listOf(
                Alert(
                    id = "alert_1",
                    subjectEn = "Lakeshore West Service Update",
                    bodyEn = "Trains are operating with minor delays due to maintenance.",
                    affectedLines = listOf("LW"),
                    affectedStops = listOf("Union Station GO"),
                    date = Clock.System.now(),
                    isRead = false,
                )
            )
        )
        provideContent {
            AlertsWidgetContent(uiState)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferencesRepository: IPreferencesRepository by inject(IPreferencesRepository::class.java)
        val transitRepository: ITransitRepository by inject(ITransitRepository::class.java)
        val viewModel = AlertsWidgetViewModel(
            transitRepository = transitRepository,
            preferencesRepository = preferencesRepository,
        )

        provideContent {
            val uiState by viewModel.uiState.collectAsState()
            AlertsWidgetContent(uiState, viewModel::refresh)
        }
    }
}

@Composable
fun AlertsWidgetContent(
    uiState: AlertsWidgetUIState,
    onRefresh: () -> Unit = {},
) {
    val language = Locale.current.language
    GlanceTheme(
        colors = ColorProviders(
            light = lightScheme,
            dark = darkScheme,
        )
    ) {
        val context = LocalContext.current
        Scaffold(
            titleBar = {
                TitleBar(
                    startIcon = ImageProvider(R.mipmap.ic_launcher),
                    iconColor = null,
                    title = context.getString(R.string.alerts),
                    modifier = GlanceModifier.clickable(
                        actionStartActivity<MainActivity>(
                            parameters = actionParametersOf(
                                alertsKey to true
                            )
                        )
                    )
                )
            },
            horizontalPadding = 0.dp,
            backgroundColor = ColorProvider(
                GlanceTheme.colors.background.getColor(context).copy(alpha = .8f)
            )
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (uiState.status) {
                    Status.ERROR -> {
                        Text(
                            text = context.getString(R.string.error),
                            style = TextDefaults.defaultTextStyle.copy(
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.onBackground,
                            ),
                            modifier = GlanceModifier.defaultWeight(),
                        )
                    }
                    Status.LOADING -> {
                        Text(
                            text = context.getString(R.string.loading),
                            style = TextDefaults.defaultTextStyle.copy(
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.onBackground,
                            ),
                            modifier = GlanceModifier.defaultWeight(),
                        )
                    }
                    Status.LOADED -> {
                        if (uiState.alerts.isEmpty()) {
                            Text(
                                text = "No active alerts",
                                style = TextDefaults.defaultTextStyle.copy(
                                    textAlign = TextAlign.Center,
                                    color = GlanceTheme.colors.onBackground,
                                ),
                                modifier = GlanceModifier.defaultWeight(),
                            )
                        } else {
                            LazyColumn(
                                modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                            ) {
                                items(uiState.alerts) { alert ->
                                    Column(
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable(
                                                actionStartActivity<MainActivity>(
                                                    parameters = actionParametersOf(
                                                        alertIdKey to alert.id
                                                    )
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = alert.getFullSubject(language),
                                            style = TextDefaults.defaultTextStyle.copy(
                                                color = GlanceTheme.colors.onBackground,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                            ),
                                            maxLines = 2,
                                        )
                                        Spacer(modifier = GlanceModifier.height(2.dp))
                                        Text(
                                            text = alert.getAnnotatedBody(
                                                language,
                                                linkColor = GlanceTheme.colors.onSurface.getColor(context)
                                            ).toString(),
                                            style = TextDefaults.defaultTextStyle.copy(
                                                color = GlanceTheme.colors.onBackground,
                                                fontSize = 12.sp,
                                            ),
                                            maxLines = 2,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                RefreshButton(
                    title = context.getString(R.string.updated, uiState.lastUpdated),
                    onClick = onRefresh,
                )
            }
        }
    }
}

private val alertIdKey = ActionParameters.Key<String>("alertId")
private val alertsKey = ActionParameters.Key<Boolean>("alerts")
