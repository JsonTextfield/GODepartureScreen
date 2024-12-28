package com.jsontextfield.departurescreen.android.ui

enum class TrainLine(
    val code: String,
    val title: String,
    val colour: Int,
) {
    RichmondHill(
        code = "RH",
        title = "Richmond Hill",
        colour = com.jsontextfield.departurescreen.ui.theme.RichmondHill
    ),
    Kitchener(
        code = "KI",
        title = "Kitchener",
        colour = com.jsontextfield.departurescreen.ui.theme.Kitchener
    ),
    Barrie(
        code = "BR",
        title = "Barrie",
        colour = com.jsontextfield.departurescreen.ui.theme.Barrie
    ),
    LakeshoreWest(
        code = "LW",
        title = "Lakeshore West",
        colour = com.jsontextfield.departurescreen.ui.theme.LakeshoreWest
    ),
    LakeshoreEast(
        code = "LE",
        title = "Lakeshore East",
        colour = com.jsontextfield.departurescreen.ui.theme.LakeshoreEast
    ),
    Milton(
        code = "MI",
        title = "Milton",
        colour = com.jsontextfield.departurescreen.ui.theme.Milton
    ),
    Stouffville(
        code = "ST",
        title = "Stouffville",
        colour = com.jsontextfield.departurescreen.ui.theme.Stouffville
    )
}