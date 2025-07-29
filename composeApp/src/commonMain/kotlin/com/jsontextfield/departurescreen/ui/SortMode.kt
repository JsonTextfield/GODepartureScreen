package com.jsontextfield.departurescreen.ui

import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.line
import departure_screen.composeapp.generated.resources.time
import org.jetbrains.compose.resources.StringResource

enum class SortMode(val key: StringResource) {
    TIME(Res.string.time),
    LINE(Res.string.line),
}