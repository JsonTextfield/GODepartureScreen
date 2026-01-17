package com.jsontextfield.departurescreen.ui.menu

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import com.jsontextfield.departurescreen.widget.ui.DeparturesWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext


actual fun addWidgetAction() {
    CoroutineScope(Dispatchers.Main).launch {
        val context = GlobalContext.get().get<Context>()
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        glanceAppWidgetManager.requestPinGlanceAppWidget(
            receiver = MyAppWidgetReceiver::class.java,
            preview = DeparturesWidget(),
        )
    }
}

actual fun isAddWidgetActionVisible(): Boolean = true

actual fun rateAppAction() {
    val browserIntent = Intent(
        Intent.ACTION_VIEW,
        "https://play.google.com/store/apps/details?id=com.jsontextfield.departurescreen.android".toUri(),
    ).apply {
        addFlags(FLAG_ACTIVITY_NEW_TASK)
    }
    val context = GlobalContext.get().get<Context>()
    context.startActivity(browserIntent)
}

actual fun isRateAppActionVisible(): Boolean = true