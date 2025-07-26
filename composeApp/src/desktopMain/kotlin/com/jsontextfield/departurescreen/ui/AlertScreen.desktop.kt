package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.Composable

@Composable
actual fun Back(onBackPressed: () -> Unit) {
    // No-op for desktop, as back handling is typically not required
    // in desktop applications like it is in mobile applications.
}