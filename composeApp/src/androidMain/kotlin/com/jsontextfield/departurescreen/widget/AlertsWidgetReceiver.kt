package com.jsontextfield.departurescreen.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.jsontextfield.departurescreen.widget.ui.AlertsWidget

class AlertsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AlertsWidget()
}
