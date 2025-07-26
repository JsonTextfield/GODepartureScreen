package com.jsontextfield.departurescreen.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun Back(onBackPressed: () -> Unit) {
    BackHandler { onBackPressed() }
}