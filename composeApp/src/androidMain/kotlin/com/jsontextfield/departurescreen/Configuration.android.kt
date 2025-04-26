package com.jsontextfield.departurescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun getScreenWidth(): Int = LocalConfiguration.current.screenWidthDp
