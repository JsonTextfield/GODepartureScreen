package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.line
import departure_screen.core.generated.resources.time
import org.jetbrains.compose.resources.StringResource

enum class SortMode(val key: StringResource) {
    TIME(Res.string.time),
    LINE(Res.string.line),
}