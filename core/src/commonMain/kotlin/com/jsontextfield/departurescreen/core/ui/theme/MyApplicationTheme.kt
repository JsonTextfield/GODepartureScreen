package com.jsontextfield.departurescreen.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.departurescreen.core.ui.ContrastMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode

@Composable
fun AppTheme(
    theme: ThemeMode = ThemeMode.DEFAULT,
    contrast: ContrastMode = ContrastMode.NORMAL,
    useDynamicTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    LocalTheme = compositionLocalOf { theme }
    val isDarkTheme = when (theme) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.DEFAULT -> isSystemInDarkTheme()
    }
    val colors = when {
        isDynamicThemeEnabled() && useDynamicTheme -> dynamicColorScheme(isDarkTheme) ?: if (isDarkTheme) darkScheme else lightScheme
        isDarkTheme && contrast == ContrastMode.NORMAL -> darkScheme
        isDarkTheme && contrast == ContrastMode.MEDIUM -> darkMediumContrastScheme
        isDarkTheme && contrast == ContrastMode.HIGH -> darkHighContrastScheme
        !isDarkTheme && contrast == ContrastMode.NORMAL -> lightScheme
        !isDarkTheme && contrast == ContrastMode.MEDIUM -> lightMediumContrastScheme
        !isDarkTheme && contrast == ContrastMode.HIGH -> lightHighContrastScheme
        else -> if (isDarkTheme) darkScheme else lightScheme
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    CompositionLocalProvider(LocalTheme provides theme) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            shapes = shapes,
            content = content,
        )
    }
}

expect fun isDynamicThemeEnabled(): Boolean

expect fun dynamicColorScheme(useDarkTheme: Boolean): ColorScheme?

lateinit var LocalTheme: ProvidableCompositionLocal<ThemeMode>
