package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.relative
import departure_screen.core.generated.resources.twelve_hour
import departure_screen.core.generated.resources.twenty_four_hour
import org.jetbrains.compose.resources.StringResource

enum class TimeFormat(val key: StringResource) {
    TWELVE_HOUR(Res.string.twelve_hour),
    TWENTY_FOUR_HOUR(Res.string.twenty_four_hour),
    RELATIVE(Res.string.relative),
}