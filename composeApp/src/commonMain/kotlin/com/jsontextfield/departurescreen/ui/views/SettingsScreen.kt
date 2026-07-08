package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.theme.isDynamicThemeEnabled
import com.jsontextfield.departurescreen.core.ui.viewmodels.SettingsViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.appearance
import departure_screen.composeapp.generated.resources.contrast
import departure_screen.composeapp.generated.resources.dynamic_theme
import departure_screen.composeapp.generated.resources.experimental
import departure_screen.composeapp.generated.resources.settings
import departure_screen.composeapp.generated.resources.theme
import departure_screen.composeapp.generated.resources.time_format
import departure_screen.composeapp.generated.resources.use_alerts_with_links
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
        contrastMode = uiState.contrastMode,
        useDynamicTheme = uiState.useDynamicTheme,
        timeFormat = uiState.timeFormat,
        useAlertsWithLinks = uiState.useAlertsWithLinks,
        onBackPressed = onBackPressed,
        onThemeChanged = settingsViewModel::onThemeModeChange,
        onContrastChanged = settingsViewModel::onContrastModeChange,
        onDynamicThemeChanged = settingsViewModel::onDynamicThemeChange,
        onTimeFormatChanged = settingsViewModel::onTimeFormatChange,
        onUseAlertsWithLinksChanged = settingsViewModel::onUseAlertsWithLinksChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    themeMode: ThemeMode = ThemeMode.DEFAULT,
    contrastMode: ContrastMode = ContrastMode.NORMAL,
    useDynamicTheme: Boolean = false,
    timeFormat: TimeFormat = TimeFormat.RELATIVE,
    useAlertsWithLinks: Boolean = false,
    onBackPressed: () -> Unit = {},
    onThemeChanged: (ThemeMode) -> Unit = {},
    onContrastChanged: (ContrastMode) -> Unit = {},
    onDynamicThemeChanged: (Boolean) -> Unit = {},
    onTimeFormatChanged: (TimeFormat) -> Unit = {},
    onUseAlertsWithLinksChanged: (Boolean) -> Unit = {},
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
            Text(
                stringResource(Res.string.appearance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            ThemeSetting(
                themeMode,
                onThemeChanged = onThemeChanged,
                modifier = Modifier.fillMaxWidth(),
            )
            ContrastSetting(
                contrastMode,
                onContrastChanged = onContrastChanged,
                isEnabled = !useDynamicTheme,
                modifier = Modifier.fillMaxWidth()
            )
            if (isDynamicThemeEnabled()) {
                SettingsSwitchItem(
                    text = stringResource(Res.string.dynamic_theme),
                    checked = useDynamicTheme,
                    onCheckedChange = onDynamicThemeChanged,
                )
            }
            TimeSetting(
                timeFormat,
                onTimeFormatChanged = onTimeFormatChanged,
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Text(
                stringResource(Res.string.experimental),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            SettingsSwitchItem(
                text = stringResource(Res.string.use_alerts_with_links),
                checked = useAlertsWithLinks,
                onCheckedChange = onUseAlertsWithLinksChanged,
            )
        }
    }
}

@Composable
private fun ThemeSetting(
    themeMode: ThemeMode,
    modifier: Modifier = Modifier,
    onThemeChanged: (ThemeMode) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(stringResource(Res.string.theme))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEach {
                SegmentedButton(
                    selected = themeMode == it,
                    onClick = { onThemeChanged(it) },
                    shape = SegmentedButtonDefaults.itemShape(it.ordinal, ThemeMode.entries.size),
                    label = {
                        Text(stringResource(it.key))
                    },
                )
            }
        }
    }
}

@Composable
private fun ContrastSetting(
    contrastMode: ContrastMode,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onContrastChanged: (ContrastMode) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(stringResource(Res.string.contrast))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ContrastMode.entries.forEach {
                SegmentedButton(
                    selected = contrastMode == it,
                    onClick = { onContrastChanged(it) },
                    shape = SegmentedButtonDefaults.itemShape(it.ordinal, ContrastMode.entries.size),
                    label = {
                        Text(stringResource(it.key))
                    },
                    enabled = isEnabled,
                )
            }
        }
    }
}

@Composable
private fun TimeSetting(
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
    onTimeFormatChanged: (TimeFormat) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(stringResource(Res.string.time_format))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TimeFormat.entries.forEach {
                SegmentedButton(
                    selected = timeFormat == it,
                    onClick = { onTimeFormatChanged(it) },
                    shape = SegmentedButtonDefaults.itemShape(it.ordinal, TimeFormat.entries.size),
                    label = {
                        Text(stringResource(it.key))
                    }
                )
            }
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

@Composable
private fun SettingsSwitchItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(text) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    )
}


@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}