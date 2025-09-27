package com.jsontextfield.departurescreen.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Back(onBackPressed: () -> Unit) {
    BackHandler { onBackPressed() }
}