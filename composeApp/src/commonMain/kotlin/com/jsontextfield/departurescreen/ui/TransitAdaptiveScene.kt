package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

private fun <T : Any> slideTransitionSpec(): AnimatedContentTransitionScope<NavEntry<T>?>.() -> ContentTransform = {
    if (initialState == null && targetState != null) {
        // Opening detail pane from right
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    } else if (initialState != null && targetState == null) {
        // Closing detail pane to right
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    } else {
        // Navigating between different details
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    }
}

data class TransitAdaptiveScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
    val stopsEntry: NavEntry<T>?,
    val alertEntry: NavEntry<T>?,
    val settingsEntry: NavEntry<T>? = null,
    val isExtraWideScreen: Boolean = false,
    val onDismissStops: () -> Unit = {},
    val onDismissAlert: () -> Unit = {},
    val onDismissSettings: () -> Unit = {}
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry, stopsEntry, alertEntry, settingsEntry)
    override val content: @Composable (() -> Unit) = {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalWidth = maxWidth
            val isDetailVisible = detailEntry != null
            val isAlertVisible = alertEntry != null

            val homeWeight by animateFloatAsState(
                targetValue = if (isAlertVisible && !isExtraWideScreen) 0.0001f else 1f,
                animationSpec = tween(durationMillis = 300)
            )
            val detailWeight by animateFloatAsState(
                targetValue = when {
                    !isDetailVisible -> 0.0001f
                    isAlertVisible && isExtraWideScreen -> 1f
                    else -> 2f
                },
                animationSpec = tween(durationMillis = 300)
            )
            val alertWeight by animateFloatAsState(
                targetValue = if (isAlertVisible) 1f else 0.0001f,
                animationSpec = tween(durationMillis = 300)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                // Main 3-Pane layout
                Row(modifier = Modifier.fillMaxSize()) {
                    if (isDetailVisible && isExtraWideScreen) {
                        Column(
                            modifier = Modifier
                                .width(totalWidth / 3)
                                .fillMaxHeight()
                                .clipToBounds()
                        ) {
                            listEntry.Content()
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(homeWeight)
                                .fillMaxHeight()
                                .clipToBounds()
                        ) {
                            listEntry.Content()
                        }
                    }

                    if (isDetailVisible) {
                        AnimatedContent(
                            targetState = detailEntry,
                            contentKey = { entry -> entry?.contentKey },
                            modifier = Modifier
                                .weight(detailWeight)
                                .fillMaxHeight(),
                            transitionSpec = slideTransitionSpec()
                        ) { entry ->
                            if (entry != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .shadow(elevation = 8.dp)
                                ) {
                                    entry.Content()
                                }
                            }
                        }
                    }

                    if (isAlertVisible || !isExtraWideScreen) {
                        AnimatedContent(
                            targetState = alertEntry,
                            contentKey = { entry -> entry?.contentKey },
                            modifier = Modifier
                                .weight(alertWeight)
                                .fillMaxHeight(),
                            transitionSpec = slideTransitionSpec()
                        ) { entry ->
                            if (entry != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .shadow(elevation = 8.dp)
                                ) {
                                    entry.Content()
                                }
                            }
                        }
                    }
                }

                // Slide-over drawer view for Stops if stopsEntry != null
                AnimatedVisibility(
                    visible = stopsEntry != null,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 1/3f))
                            .clickable(onClick = onDismissStops)
                    )
                }

                AnimatedVisibility(
                    visible = stopsEntry != null,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .fillMaxWidth(1/3f)
                                .widthIn(max = 480.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(16.dp)
                                .clickable(enabled = false, onClick = {})
                        ) {
                            stopsEntry?.Content()
                        }
                    }
                }

                // Slide-over drawer view for Settings if settingsEntry != null
                AnimatedVisibility(
                    visible = settingsEntry != null,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 1/3f))
                            .clickable(onClick = onDismissSettings)
                    )
                }

                AnimatedVisibility(
                    visible = settingsEntry != null,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .fillMaxWidth(1/3f)
                                .widthIn(max = 480.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(16.dp)
                                .clickable(enabled = false, onClick = {})
                        ) {
                            settingsEntry?.Content()
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun homePane() = metadata { put(HomeKey, true) }
        fun tripPane() = metadata { put(TripKey, true) }
        fun alertPane() = metadata { put(AlertKey, true) }
    }

    object HomeKey : NavMetadataKey<Boolean>
    object TripKey : NavMetadataKey<Boolean>
    object AlertKey : NavMetadataKey<Boolean>
}

class TransitAdaptiveSceneStrategy<T : Any>(
    val isWideScreen: Boolean,
    val isExtraWideScreen: Boolean,
    val onDismissAlert: () -> Unit,
    val onDismissStops: () -> Unit = {},
    val onDismissSettings: () -> Unit = {}
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val homeEntry = entries.findLast { it.metadata.contains(TransitAdaptiveScene.HomeKey) } ?: return null

        val tripEntry = entries.findLast { it.metadata.contains(TransitAdaptiveScene.TripKey) }

        val topEntry = entries.lastOrNull()
        if (topEntry != null && topEntry != homeEntry) {
            if ((topEntry.metadata.contains(AlertsAdaptiveScene.AlertsKey) && tripEntry == null) ||
                (topEntry.metadata.contains(TransitAdaptiveScene.AlertKey) && tripEntry == null)
            ) {
                return null
            }
            if (!isWideScreen && (topEntry.metadata.contains(TransitAdaptiveScene.TripKey) ||
                        topEntry.metadata.contains(SettingsDrawerScene.SettingsKey) ||
                        topEntry.metadata.contains(StopsDrawerScene.StopsKey))) {
                return null
            }
        }

        val stopsEntry = entries.findLast { it.metadata.contains(StopsDrawerScene.StopsKey) }
        val settingsEntry = entries.findLast { it.metadata.contains(SettingsDrawerScene.SettingsKey) }
        val alertEntry = if (tripEntry != null) {
            entries.lastOrNull()?.takeIf { it.metadata.contains(TransitAdaptiveScene.AlertKey) }
        } else null

        val sceneKey = "TransitAdaptiveScene"

        return TransitAdaptiveScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = homeEntry,
            detailEntry = if (isWideScreen) tripEntry else null,
            stopsEntry = stopsEntry,
            settingsEntry = settingsEntry,
            alertEntry = alertEntry,
            isExtraWideScreen = isExtraWideScreen,
            onDismissStops = onDismissStops,
            onDismissAlert = onDismissAlert,
            onDismissSettings = onDismissSettings
        )
    }
}

data class AlertsAdaptiveScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val alertsEntry: NavEntry<T>,
    val alertDetailsEntry: NavEntry<T>?,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(alertsEntry, alertDetailsEntry)
    override val content: @Composable (() -> Unit) = {
        val isDetailVisible = alertDetailsEntry != null
        val detailWeight by animateFloatAsState(
            targetValue = if (isDetailVisible) 1f else 0.0001f,
            animationSpec = tween(durationMillis = 300)
        )

        // Alerts + Alert Details: 2/3 list (alerts), 1/3 detail (alertDetails)
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                alertsEntry.Content()
            }

            AnimatedContent(
                targetState = alertDetailsEntry,
                contentKey = { entry -> entry?.contentKey },
                modifier = Modifier
                    .weight(detailWeight)
                    .fillMaxHeight(),
                transitionSpec = slideTransitionSpec()
            ) { entry ->
                if (entry != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .shadow(elevation = 8.dp)
                    ) {
                        entry.Content()
                    }
                }
            }
        }
    }

    companion object {
        fun alertsPane() = metadata { put(AlertsKey, true) }
        fun alertDetailsPane() = metadata { put(AlertDetailsKey, true) }
    }

    object AlertsKey : NavMetadataKey<Boolean>
    object AlertDetailsKey : NavMetadataKey<Boolean>
}

class AlertsAdaptiveSceneStrategy<T : Any>(
    val isWideScreen: Boolean
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!isWideScreen) return null

        // If TripDetailsRoute is in entries, let TransitAdaptiveSceneStrategy handle it
        if (entries.any { it.metadata.contains(TransitAdaptiveScene.TripKey) }) return null

        val alertsEntry = entries.findLast { it.metadata.contains(AlertsAdaptiveScene.AlertsKey) } ?: return null
        val alertDetailsEntry = entries.findLast { it.metadata.contains(AlertsAdaptiveScene.AlertDetailsKey) }

        val sceneKey = "AlertsAdaptiveScene"

        return AlertsAdaptiveScene(
            key = sceneKey,
            previousEntries = entries.takeWhile { it != alertsEntry },
            alertsEntry = alertsEntry,
            alertDetailsEntry = alertDetailsEntry
        )
    }
}

data class DrawerScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val underlyingEntry: NavEntry<T>,
    val drawerEntry: NavEntry<T>,
    val alignment: Alignment,
    val slideInInitialOffset: (Int) -> Int,
    val slideOutTargetOffset: (Int) -> Int,
    val onDismiss: () -> Unit = {}
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(underlyingEntry, drawerEntry)
    override val content: @Composable (() -> Unit) = {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            visible = true
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Underlying screen underneath remains static
            underlyingEntry.Content()

            // Scrim background with fade
            val scrimAlpha by animateFloatAsState(if (visible) 1/3f else 0f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(onClick = onDismiss)
            )

            // Sliding drawer panel
            Box(
                modifier = Modifier
                    .align(alignment)
                    .fillMaxHeight()
                    .widthIn(min = 360.dp, max = 480.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .shadow(16.dp)
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(initialOffsetX = slideInInitialOffset),
                    exit = slideOutHorizontally(targetOffsetX = slideOutTargetOffset),
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawerEntry.Content()
                }
            }
        }
    }
}

object StopsDrawerScene {
    fun stopsPane() = metadata { put(StopsKey, true) }
    object StopsKey : NavMetadataKey<Boolean>
}

class StopsDrawerSceneStrategy<T : Any>(
    val isWideScreen: Boolean,
    val onDismissStops: () -> Unit
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!isWideScreen) return null

        val stopsEntry = entries.lastOrNull()?.takeIf { it.metadata.contains(StopsDrawerScene.StopsKey) } ?: return null
        if (entries.size < 2) return null

        val underlyingEntry = entries[entries.size - 2]

        return DrawerScene(
            key = underlyingEntry.contentKey,
            previousEntries = entries.dropLast(1),
            underlyingEntry = underlyingEntry,
            drawerEntry = stopsEntry,
            alignment = Alignment.CenterStart,
            slideInInitialOffset = { -it },
            slideOutTargetOffset = { -it },
            onDismiss = onDismissStops
        )
    }
}

object SettingsDrawerScene {
    fun settingsPane() = metadata { put(SettingsKey, true) }
    object SettingsKey : NavMetadataKey<Boolean>
}

class SettingsDrawerSceneStrategy<T : Any>(
    val isWideScreen: Boolean,
    val onDismissSettings: () -> Unit
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!isWideScreen) return null

        val settingsEntry =
            entries.lastOrNull()?.takeIf { it.metadata.contains(SettingsDrawerScene.SettingsKey) } ?: return null
        if (entries.size < 2) return null

        val underlyingEntry = entries[entries.size - 2]

        return DrawerScene(
            key = settingsEntry.contentKey,
            previousEntries = entries.dropLast(1),
            underlyingEntry = underlyingEntry,
            drawerEntry = settingsEntry,
            alignment = Alignment.CenterEnd,
            slideInInitialOffset = { it },
            slideOutTargetOffset = { it },
            onDismiss = onDismissSettings
        )
    }
}
