package com.jsontextfield.departurescreen.ui.menu

import android.content.Context
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