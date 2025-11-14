package com.jsontextfield.departurescreen.core.ui

import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.bus
import departure_screen.core.generated.resources.train
import org.jetbrains.compose.resources.StringResource

enum class StationType(
    val stringResId: StringResource,
) {
    BUS(Res.string.bus),
    TRAIN(Res.string.train),
}