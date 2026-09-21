package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.UIApplication
import swiftPMImport.Departure_Screen.composeApp.GADAdSizeFromCGSize
import swiftPMImport.Departure_Screen.composeApp.GADBannerView
import swiftPMImport.Departure_Screen.composeApp.GADRequest
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
private val BANNER_AD_UNIT_ID = if (Platform.isDebugBinary) {
    "ca-app-pub-3940256099942544/2934735716"
} else {
    BANNER_AD_UNIT_ID_RELEASE
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun BannerAd(modifier: Modifier) {
    UIKitView(
        factory = {
            GADBannerView(
                adSize = GADAdSizeFromCGSize(
                    CGSizeMake(
                        320.0,
                        50.0
                    )
                )
            ).apply {
                adUnitID = BANNER_AD_UNIT_ID
                // The banner needs a root view controller to present overlays
                // (e.g. when a user taps the ad).
                rootViewController =
                    UIApplication.sharedApplication.keyWindow?.rootViewController
                loadRequest(GADRequest())
            }
        },
        modifier = modifier.fillMaxWidth().height(50.dp),
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}