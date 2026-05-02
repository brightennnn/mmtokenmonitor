package io.github.brightennnn.mmtokenmonitor.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.os.Build
import org.json.JSONObject
import java.io.File

/**
 * 根据 app 的主题设置生成颜色
 * 读取 widget_data.json 中的主题设置（dynamicColor, darkMode, pureBlack）
 */
object MonetColorExtractor {

    private val DEFAULT_ACCENT_COLOR = 0xFF6750A4.toInt()
    private const val WIDGET_DATA_FILE = "widget_data.json"

    private data class ThemeSettings(
        val dynamicColor: Boolean,
        val darkMode: Int, // 0=follow system, 1=light, 2=dark
        val pureBlack: Boolean,
        val paletteStyle: Int,
        val contrastLevel: Int
    )

    private fun readThemeSettings(context: Context): ThemeSettings? {
        val file = File(context.filesDir, WIDGET_DATA_FILE)
        if (!file.exists()) return null
        return try {
            val json = JSONObject(file.readText())
            ThemeSettings(
                dynamicColor = json.optBoolean("dynamicColor", true),
                darkMode = json.optInt("darkMode", 0),
                pureBlack = json.optBoolean("pureBlack", false),
                paletteStyle = json.optInt("paletteStyle", 1),
                contrastLevel = json.optInt("contrastLevel", 2)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun isDarkTheme(context: Context, settings: ThemeSettings): Boolean {
        return when (settings.darkMode) {
            1 -> false  // Light
            2 -> true   // Dark
            else -> {   // Follow system
                val flags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                flags == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    fun getAccentColor(context: Context): Int {
        val settings = readThemeSettings(context)
        val isDarkTheme = settings?.let { isDarkTheme(context, it) } ?: false

        // 如果是纯黑模式，accent 用白色（高亮）
        if (settings?.pureBlack == true && isDarkTheme) {
            return Color.White.toArgb()
        }

        return try {
            if (settings?.dynamicColor == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val colorScheme = if (isDarkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
                colorScheme.primary.toArgb()
            } else {
                DEFAULT_ACCENT_COLOR
            }
        } catch (e: Exception) {
            DEFAULT_ACCENT_COLOR
        }
    }

    fun getBackgroundColor(context: Context): Int {
        val settings = readThemeSettings(context)
        val isDarkTheme = settings?.let { isDarkTheme(context, it) } ?: false

        // 纯黑模式
        if (settings?.pureBlack == true && isDarkTheme) {
            return Color.Black.toArgb()
        }

        return try {
            if (settings?.dynamicColor == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val colorScheme = if (isDarkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
                colorScheme.background.toArgb()
            } else {
                if (isDarkTheme) {
                    Color(0xFF121212).toArgb()
                } else {
                    Color(0xFFFFFFFF).toArgb()
                }
            }
        } catch (e: Exception) {
            if (isDarkTheme) {
                Color(0xFF121212).toArgb()
            } else {
                Color(0xFFFFFFFF).toArgb()
            }
        }
    }

    fun getTextColor(context: Context): Int {
        val settings = readThemeSettings(context)
        val isDarkTheme = settings?.let { isDarkTheme(context, it) } ?: false

        // 纯黑模式下文字用白色
        if (settings?.pureBlack == true && isDarkTheme) {
            return Color.White.toArgb()
        }

        return if (isDarkTheme) Color.White.toArgb() else Color.Black.toArgb()
    }

    fun getSecondaryTextColor(context: Context): Int {
        val settings = readThemeSettings(context)
        val isDarkTheme = settings?.let { isDarkTheme(context, it) } ?: false

        // 纯黑模式下次要文字用灰色
        if (settings?.pureBlack == true && isDarkTheme) {
            return Color(0xFF9E9E9E).toArgb()
        }

        return if (isDarkTheme) Color(0xFFB0B0B0).toArgb() else Color(0xFF666666).toArgb()
    }
}
