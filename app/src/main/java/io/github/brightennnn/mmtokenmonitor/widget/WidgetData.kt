package io.github.brightennnn.mmtokenmonitor.widget

data class WidgetData(
    val modelName: String = "—",
    val intervalUsed: Int = 0,
    val intervalTotal: Int = 0,
    val intervalStartMs: Long = 0L,
    val intervalEndMs: Long = 0L,
    val weeklyUsed: Int = 0,
    val weeklyTotal: Int = 0,
    val weeklyStartMs: Long = 0L,
    val weeklyEndMs: Long = 0L,
    val lastUpdate: String = "—",
    val intervalPeriod: String = "—",
    val weeklyPeriod: String = "—",
    // Widget 字体大小
    val widgetTitleSize: Int = 14,
    val widgetNumberSize: Int = 12,
    val widgetProgressBarSize: Int = 10,
    val widgetPercentSize: Int = 10,
    val widgetUpdateTimeSize: Int = 8
)
