package com.jsontextfield.departurescreen.ui.menu

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.jsontextfield.departurescreen.widget.DeparturesWidget
import com.jsontextfield.departurescreen.widget.MyAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

actual fun addWidgetAction() {
    CoroutineScope(Dispatchers.Main).launch {
        val context = GlobalContext.get().get<Context>()
        GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
            receiver = MyAppWidgetReceiver::class.java,
            preview = DeparturesWidget(),
            previewState = DpSize(300.dp, 200.dp)
        )
    }
}

actual fun isAddWidgetActionVisible(): Boolean = true