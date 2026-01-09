package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.sort_by_line
import departure_screen.core.generated.resources.sort_by_time
import org.jetbrains.compose.resources.StringResource

enum class SortMode(val key: StringResource) {
    TIME(Res.string.sort_by_time),
    LINE(Res.string.sort_by_line),
}