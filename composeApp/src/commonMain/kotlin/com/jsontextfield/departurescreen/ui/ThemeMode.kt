package com.jsontextfield.departurescreen.ui

import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.dark_mode
import departure_screen.composeapp.generated.resources.light_mode
import departure_screen.composeapp.generated.resources.system_default
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode(val key: StringResource) {
    LIGHT(Res.string.light_mode),
    DARK(Res.string.dark_mode),
    DEFAULT(Res.string.system_default),
}