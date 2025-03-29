package com.jsontextfield.departurescreen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.jsontextfield.departurescreen.di.initKoin
import com.jsontextfield.departurescreen.ui.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport(document.body!!) {
        App()
    }
}