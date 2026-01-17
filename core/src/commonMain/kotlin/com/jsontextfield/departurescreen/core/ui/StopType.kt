package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.bus
import departure_screen.core.generated.resources.train
import org.jetbrains.compose.resources.StringResource

enum class StopType(
    val stringResId: StringResource,
    val typeString: String,
) {
    BUS(Res.string.bus, "Bus"),
    TRAIN(Res.string.train, "Train"),
}