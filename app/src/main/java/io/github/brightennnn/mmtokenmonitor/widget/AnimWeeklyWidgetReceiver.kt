package io.github.brightennnn.mmtokenmonitor.widget

class AnimWeeklyWidgetReceiver : android.appwidget.AppWidgetProvider() {
    override fun onUpdate(
        context: android.content.Context,
        manager: android.appwidget.AppWidgetManager,
        ids: IntArray
    ) {
        for (id in ids) {
            AnimWeeklyWidget.updateWidget(context, manager, id)
        }
    }

    override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
        super.onReceive(context, intent)
        if (intent.action == AnimWeeklyWidget.ACTION_REFRESH_B) {
            val ids = intent.getIntArrayExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS)
            ids?.forEach { id ->
                val manager = android.appwidget.AppWidgetManager.getInstance(context)
                AnimWeeklyWidget.updateWidget(context, manager, id)
            }
        }
    }
}
