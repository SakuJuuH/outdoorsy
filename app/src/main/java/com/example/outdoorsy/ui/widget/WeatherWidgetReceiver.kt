package com.example.outdoorsy.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.outdoorsy.ui.widget.worker.WeatherWidgetWorker

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        enqueueUpdate(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        enqueueUpdate(context)
    }

    private fun enqueueUpdate(context: Context) {
        val request = OneTimeWorkRequestBuilder<WeatherWidgetWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "weather_widget_update",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
