package com.jsontextfield.departurescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import platform.UIKit.UIScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidth(): Int = LocalWindowInfo.current.containerSize.width.pxToPoint().toInt()

fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale