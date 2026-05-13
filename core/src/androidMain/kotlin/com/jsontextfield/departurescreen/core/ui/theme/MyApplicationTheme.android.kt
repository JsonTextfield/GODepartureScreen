package com.jsontextfield.departurescreen.core.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import org.koin.core.context.GlobalContext

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
actual fun isDynamicThemeEnabled(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

actual fun dynamicColorScheme(useDarkTheme: Boolean): ColorScheme? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = GlobalContext.get().get<Context>()
        return if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    return null
}