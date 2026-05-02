package io.github.brightennnn.mmtokenmonitor.widget

import android.content.Context
import androidx.compose.ui.graphics.Color

/**
 * Widget 颜色配置，根据 app 主题设置生成
 */
data class WidgetColors(
    val background: Color,
    val primary: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val surfaceVariant: Color,
    val outline: Color
) {
    companion object {
        fun fromContext(context: Context): WidgetColors {
            val accentColor = Color(MonetColorExtractor.getAccentColor(context))
            val bgColor = Color(MonetColorExtractor.getBackgroundColor(context))
            val textColor = Color(MonetColorExtractor.getTextColor(context))
            val secondaryTextColor = Color(MonetColorExtractor.getSecondaryTextColor(context))

            // 纯黑模式下 surfaceVariant 也是黑色
            val isPureBlack = try {
                val file = java.io.File(context.filesDir, "widget_data.json")
                if (file.exists()) {
                    val json = org.json.JSONObject(file.readText())
                    json.optBoolean("pureBlack", false)
                } else false
            } catch (e: Exception) {
                false
            }
            val surfaceVariant = if (isPureBlack) Color.Black else bgColor

            return WidgetColors(
                background = bgColor,
                primary = accentColor,
                onSurface = textColor,
                onSurfaceVariant = secondaryTextColor,
                surfaceVariant = surfaceVariant,
                outline = secondaryTextColor.copy(alpha = 0.5f)
            )
        }
    }
}
