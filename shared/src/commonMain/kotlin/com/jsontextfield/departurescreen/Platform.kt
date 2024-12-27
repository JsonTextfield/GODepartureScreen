package com.jsontextfield.departurescreen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform