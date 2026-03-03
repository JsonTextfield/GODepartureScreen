package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.viewmodels.SettingsViewModel
import com.jsontextfield.departurescreen.ui.menu.RadioMenuItem
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.settings
import departure_screen.composeapp.generated.resources.theme
import departure_screen.composeapp.generated.resources.time_format
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
    onBackPressed: () -> Unit = {},
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    SettingsScreen(
        themeMode = uiState.themeMode,
        timeFormat = uiState.timeFormat,
        onBackPressed = onBackPressed,
        onThemeChanged = settingsViewModel::onThemeModeChange,
        onTimeFormatChanged = settingsViewModel::onTimeFormatChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    themeMode: ThemeMode = ThemeMode.DEFAULT,
    timeFormat: TimeFormat = TimeFormat.RELATIVE,
    onBackPressed: () -> Unit = {},
    onThemeChanged: (ThemeMode) -> Unit = {},
    onTimeFormatChanged: (TimeFormat) -> Unit = {},
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(stringResource(Res.string.settings))
        }, navigationIcon = {
            BackButton(onBackPressed)
        })
    }) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(it)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeSetting(themeMode, onThemeChanged = onThemeChanged)
            TimeSetting(timeFormat, onTimeFormatChanged = onTimeFormatChanged)
        }
    }
}

@Composable
private fun ThemeSetting(themeMode: ThemeMode, onThemeChanged: (ThemeMode) -> Unit = {}) {
    Column {
        Text(stringResource(Res.string.theme))
        ThemeMode.entries.forEach {
            RadioMenuItem(
                title = stringResource(it.key),
                isSelected = themeMode == it,
                onClick = { onThemeChanged(it) })
        }
    }
}

@Composable
private fun TimeSetting(timeFormat: TimeFormat, onTimeFormatChanged: (TimeFormat) -> Unit = {}) {
    Column {
        Text(stringResource(Res.string.time_format))
        TimeFormat.entries.forEach {
            RadioMenuItem(
                title = stringResource(it.key),
                isSelected = timeFormat == it,
                onClick = { onTimeFormatChanged(it) })
        }
    }
}

@Composable
private fun RadioListItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(false, onClick = {})
        Text(text)
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}