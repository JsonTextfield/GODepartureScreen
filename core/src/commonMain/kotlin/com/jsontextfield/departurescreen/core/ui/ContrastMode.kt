package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.high
import departure_screen.core.generated.resources.medium
import departure_screen.core.generated.resources.normal
import org.jetbrains.compose.resources.StringResource

enum class ContrastMode(val key: StringResource) {
    NORMAL(Res.string.normal),
    MEDIUM(Res.string.medium),
    HIGH(Res.string.high),
}