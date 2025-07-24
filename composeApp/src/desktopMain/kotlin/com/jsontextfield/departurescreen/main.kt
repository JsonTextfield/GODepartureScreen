package com.jsontextfield.departurescreen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jsontextfield.departurescreen.di.initKoin
import com.jsontextfield.departurescreen.ui.App

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Union GO Departures",
    ) {
        App()
    }
}