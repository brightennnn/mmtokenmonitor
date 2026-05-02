package io.github.brightennnn.mmtokenmonitor.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking

/**
 * 接收 AlarmManager 定时刷新信号，刷新所有 widget
 */
class WidgetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == WidgetRefreshScheduler.ACTION_REFRESH) {
            AnimWeeklyWidget.updateAllWidgets(context)
            CanvasIntervalWidget.updateAllWidgets(context)

            // 刷新 Glance widgets
            runBlocking {
                GlanceIntervalWidget().updateAll(context)
                GlanceWeeklyWidget().updateAll(context)
            }

            // 刷新完成后重新设定下一个闹钟
            WidgetRefreshScheduler.rescheduleNext(context)
        }
    }
}
