package com.chan.volumewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.widget.RemoteViews
import timber.log.Timber


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        Timber.d("onUpdate >>> ")

        appWidgetIds.forEach { _ ->
            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setOnClickPendingIntent(R.id.btn_plus, volumeUpPendingIntent(context))
                setOnClickPendingIntent(R.id.btn_minus, volumeDownPendingIntent(context))
            }
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("onReceive >>> $intent")
        when (intent.action) {
            WidgetBroadCast.VOLUME_UP.toString() -> {
                Timber.d("volume is up 1")
                musicVolumeControl(context, true)
            }
            WidgetBroadCast.VOLUME_DOWN.toString() -> {
                Timber.d("volume is down 1")
                musicVolumeControl(context, false)
            }
        }
    }

    private fun volumeUpPendingIntent(context: Context): PendingIntent {
        return Intent(context, WidgetProvider::class.java).let { intent ->
            intent.action = WidgetBroadCast.VOLUME_UP.toString()
            PendingIntent.getBroadcast(
                    context,
                    0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

    private fun volumeDownPendingIntent(context: Context): PendingIntent {
        return Intent(context, WidgetProvider::class.java).let { intent ->
            intent.action = WidgetBroadCast.VOLUME_DOWN.toString()
            PendingIntent.getBroadcast(
                    context,
                    0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }

    private fun musicVolumeControl(context: Context, isRaise: Boolean) {
        val audioManager: AudioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val minVolume = 0
        val presentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        Timber.d("musicVolumeControl >>> minVolume is $minVolume maxVolume is $maxVolume")
        Timber.d("musicVolumeControl >>> presentMusicVolume is $presentMusicVolume")

        val volume = if (isRaise) {
            AudioManager.ADJUST_RAISE
        } else {
            AudioManager.ADJUST_LOWER
        }

        audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                volume,
                0
        )

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, WidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        allWidgetIds.forEach { _ ->
            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                setOnClickPendingIntent(R.id.btn_plus, volumeUpPendingIntent(context))
                setOnClickPendingIntent(R.id.btn_minus, volumeDownPendingIntent(context))
            }
            appWidgetManager.updateAppWidget(allWidgetIds, views)
        }
    }
}