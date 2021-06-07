package com.chan.volumewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import timber.log.Timber

class WidgetProvider : AppWidgetProvider() {

    private val MY_ACTION = "android.action.MY_ACTION"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("onUpdate >>> ")

        appWidgetIds.forEach { appwidgetId ->
            val views: RemoteViews = addViews(context)
            appWidgetManager?.updateAppWidget(appWidgetIds, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    private fun addViews(context: Context?): RemoteViews {
        val views = RemoteViews(context?.packageName, R.layout.widget_layout)
        views.setOnClickPendingIntent(R.id.btn_plus, setMyAction(context))
        views.setOnClickPendingIntent(R.id.btn_minus, buildURIIntent(context))
        return views
    }

    private fun setMyAction(context: Context?): PendingIntent {
        val intent = Intent(MY_ACTION)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildURIIntent(context: Context?): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://parkbeommin.github.io"))
        return PendingIntent.getActivity(context, 0, intent, 0)
    }


}