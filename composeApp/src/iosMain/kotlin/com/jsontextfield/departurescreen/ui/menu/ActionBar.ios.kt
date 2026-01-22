package com.jsontextfield.departurescreen.ui.menu

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun addWidgetAction() {
}

actual fun isAddWidgetActionVisible(): Boolean = false
actual fun rateAppAction() {
    val url = NSURL.URLWithString(URLString = "https://apps.apple.com/ca/app/go-departures/id6744908306")
    url?.let {
        UIApplication.sharedApplication.openURL(it, options = emptyMap<Any?, Any>(), completionHandler = null)
    }
}

actual fun isRateAppActionVisible(): Boolean = true