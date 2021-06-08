package com.chan.volumewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
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
        val thisWidget = ComponentName(context, WidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (widgetId in allWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.btn_plus, setMyAction(context))
            views.setOnClickPendingIntent(R.id.btn_minus, buildURIIntent(context))
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("onReceive >>> ${intent.data}")
        val action = intent.action
//        if (action == MY_ACTION) {
//            //버튼 클릭 결과를 로그로 확인합니다.
//            Timber.d("onReceive >>> 이벤트클릭 테스트")
//            val appWidgetManager = AppWidgetManager.getInstance(context)
//            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
//            val componentName = ComponentName(context, WidgetProvider::class.java)
//            remoteViews.setTextViewText(R.id.btn_plus, "이벤트발생!")
//            appWidgetManager.updateAppWidget(componentName, remoteViews)
//        }
    }

    private fun setMyAction(context: Context?): PendingIntent {
        Timber.d("PendingIntent setMyAction >>> ")
        val intent = Intent(MY_ACTION)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildURIIntent(context: Context?): PendingIntent {
        Timber.d("PendingIntent buildURIIntent >>> ")
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://parkbeommin.github.io"))
        return PendingIntent.getActivity(context, 0, intent, 0)
    }



}