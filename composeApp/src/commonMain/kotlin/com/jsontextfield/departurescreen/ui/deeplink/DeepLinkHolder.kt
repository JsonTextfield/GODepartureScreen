package com.jsontextfield.departurescreen.ui.deeplink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DeepLinkHolder {
    private val _pendingUrl = MutableStateFlow<String?>(null)
    val pendingUrl: StateFlow<String?> = _pendingUrl.asStateFlow()

    fun handle(url: String) {
        _pendingUrl.value = url
    }

    fun consume() {
        _pendingUrl.value = null
    }
}