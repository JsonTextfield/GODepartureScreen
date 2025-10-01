package com.jsontextfield.departurescreen.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.GoTrainDataSource
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.entities.toCombinedStation
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.ui.theme.primaryLight
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            MyContent()
        }
    }

    @Composable
    private fun MyContent(viewModel: MyViewModel = MyViewModel()) {
        val trips by viewModel.trips
        LaunchedEffect(Unit) {
            viewModel.getStations()
        }
        LazyColumn(
            modifier = GlanceModifier.background(Color.White.copy(alpha = .8f)).appWidgetBackground(),
        ) {
            item {
                Text(viewModel.selectedStation?.name.orEmpty(), modifier = GlanceModifier.padding(24.dp))
            }
            items(trips.take(3)) { trip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${trip.departureDiffMinutes}\nmin",
                        style = TextDefaults.defaultTextStyle.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.padding(8.dp)
                    )
                    Spacer(modifier = GlanceModifier.width(12.dp))
                    Box(
                        modifier = GlanceModifier
                            .size(40.dp)
                            .background(trip.color)
                            .cornerRadius(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = trip.code,
                            style = TextDefaults.defaultTextStyle.copy(
                                color = ColorProvider(Color.White),
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(12.dp))
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .defaultWeight(),
                    ) {
                        Text(trip.destination)
                        if (trip.isCancelled) {
                            Text(text = "Cancelled")
                        } else if (trip.isExpress) {
                            Text(text = "Express")
                        }
                    }
                    Spacer(modifier = GlanceModifier.width(12.dp))
                    Text(
                        text = trip.platform,
                        style = TextDefaults.defaultTextStyle.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = if ("-" in trip.platform) {
                                FontWeight.Normal
                            } else {
                                FontWeight.Bold
                            },
                            color = ColorProvider(primaryLight)
                        ),
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .defaultWeight(),
                    )
                }
            }
            item {
                Button(
                    "Updated ${viewModel.lastUpdated}",
                    { viewModel.getTrips() },
                    modifier = GlanceModifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorProvider(primaryLight),
                        contentColor = ColorProvider(Color.White)
                    )
                )
            }
        }
    }
}

class MyViewModel : ViewModel() {
    var selectedStation by mutableStateOf<CombinedStation?>(null)
    val goTrainDataSource = GoTrainDataSource(DepartureScreenAPI())
    var trips = mutableStateOf<List<Trip>>(emptyList())
    var lastUpdated: String by mutableStateOf<String>("")

    var stations = mutableStateOf<List<CombinedStation>>(emptyList())

    fun getStations() {
        viewModelScope.launch {
            stations.value = goTrainDataSource.getAllStations()
                .groupBy { it.name }
                .map { it.value.toCombinedStation() }
            getTrips()
        }
    }

    fun getTrips() {
        lastUpdated = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time
            .format(LocalTime.Format {
                byUnicodePattern("HH:mm")
            })
        selectedStation = stations.value.random()
        viewModelScope.launch {
            trips.value = selectedStation?.codes?.flatMap {
                goTrainDataSource.getTrains(it)
            }.orEmpty()
        }
    }
}