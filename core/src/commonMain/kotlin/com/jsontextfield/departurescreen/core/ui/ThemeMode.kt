package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.dark_mode
import departure_screen.core.generated.resources.light_mode
import departure_screen.core.generated.resources.system_default
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode(val key: StringResource) {
    LIGHT(Res.string.light_mode),
    DARK(Res.string.dark_mode),
    DEFAULT(Res.string.system_default),
}