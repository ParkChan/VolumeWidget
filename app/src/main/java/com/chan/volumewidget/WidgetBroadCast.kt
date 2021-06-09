package com.chan.volumewidget

enum class WidgetBroadCast(val volume: String) {
    VOLUME_UP("android.action.VOLUME_UP"),
    VOLUME_DOWN("android.action.VOLUME_DOWN")
}