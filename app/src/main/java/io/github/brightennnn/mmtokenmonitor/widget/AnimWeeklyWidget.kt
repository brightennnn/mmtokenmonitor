package io.github.brightennnn.mmtokenmonitor.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.RemoteViews
import io.github.brightennnn.mmtokenmonitor.MainActivity
import io.github.brightennnn.mmtokenmonitor.R
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepository
import io.github.brightennnn.mmtokenmonitor.data.repository.FontSizeRepositoryEntryPoint
import dagger.hilt.EntryPoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.File

/**
 * 本周用量 Widget — 竖条填满整个 widget
 * 用 ImageView + Canvas 画进度条，支持 Monet 动态颜色
 */
class AnimWeeklyWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_B) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: return
            ids.forEach { updateWidget(context, appWidgetManager, it) }
        }
    }

    override fun onEnabled(context: Context) {
        WidgetRefreshScheduler.schedule(context)
    }

    override fun onDisabled(context: Context) {
        WidgetRefreshScheduler.cancel(context)
    }

    companion object {
        const val WIDGET_DATA_FILE = "widget_data.json"
        const val ACTION_REFRESH_B = "io.github.brightennnn.mmtokenmonitor.ACTION_REFRESH_WIDGET_B"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, AnimWeeklyWidget::class.java)
            )
            ids.forEach { updateWidget(context, manager, it) }
        }

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val data = readWidgetData(context)

            // 剩余百分比（进度条从下往上填，已用在下=剩余在上）
            val pct = if (data.weeklyTotal > 0) {
                ((data.weeklyTotal - data.weeklyUsed).toFloat() / data.weeklyTotal * 100).toInt().coerceIn(0, 100)
            } else 0

            val views = RemoteViews(context.packageName, R.layout.anim_weekly_widget)

            // 动态取 Monet 颜色并画进度条
            val accentColor = MonetColorExtractor.getAccentColor(context)
            val bgColor = MonetColorExtractor.getBackgroundColor(context)
            val progressBitmap = drawVerticalProgressBar(pct, accentColor, bgColor)
            views.setImageViewBitmap(R.id.progress_bar, progressBitmap)

            // 点击跳APP
            val pendingIntent = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /**
         * 用 Canvas 画竖向进度条（从下往上填）
         */
        private fun drawVerticalProgressBar(fillPercent: Int, accentColor: Int, bgColor: Int): Bitmap {
            val width = 200
            val height = 300
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 背景（半透明）
            val bgPaint = Paint().apply { color = bgColor; style = Paint.Style.FILL }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // 进度（从下往上）
            val fillHeight = (height * fillPercent / 100f).toInt()
            val accentPaint = Paint().apply { color = accentColor; style = Paint.Style.FILL }
            canvas.drawRect(
                0f,
                (height - fillHeight).toFloat(),
                width.toFloat(),
                height.toFloat(),
                accentPaint
            )

            return bitmap
        }

        private fun isDarkTheme(context: Context): Boolean {
            return try {
                val entryPoint = EntryPoints.get(context, FontSizeRepositoryEntryPoint::class.java)
                val repo = entryPoint.fontSizeRepository()
                val monetMode = runBlocking { repo.monetMode.first() }
                when (monetMode) {
                    "dark" -> true
                    "light" -> false
                    else -> {
                        val flags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                        flags == Configuration.UI_MODE_NIGHT_YES
                    }
                }
            } catch (e: Exception) {
                val flags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                flags == Configuration.UI_MODE_NIGHT_YES
            }
        }

        private fun readWidgetData(context: Context): WidgetData {
            val file = File(context.filesDir, WIDGET_DATA_FILE)
            if (!file.exists()) return WidgetData()
            return try {
                val json = JSONObject(file.readText())
                WidgetData(
                    weeklyUsed = json.optInt("weeklyUsed", 0),
                    weeklyTotal = json.optInt("weeklyTotal", 0)
                )
            } catch (e: Exception) {
                WidgetData()
            }
        }
    }
}