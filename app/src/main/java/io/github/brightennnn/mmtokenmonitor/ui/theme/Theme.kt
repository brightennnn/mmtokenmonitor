package io.github.brightennnn.mmtokenmonitor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.brightennnn.mmtokenmonitor.domain.model.ContrastLevel
import io.github.brightennnn.mmtokenmonitor.domain.model.DarkMode
import io.github.brightennnn.mmtokenmonitor.domain.model.PaletteStyle
import io.github.brightennnn.mmtokenmonitor.domain.model.ThemeSettings

/**
 * MD3E Color Scheme — 扩展 Material3 ColorScheme
 * 支持 MD3E 特有的调色板风格和对比度级别
 */
data class MD3EColors(
    val contrastLevel: ContrastLevel = ContrastLevel.Default,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val pureBlack: Boolean = false
)

val LocalMD3EColors = staticCompositionLocalOf { MD3EColors() }

@Composable
fun MiniMaxTokenTheme(
    themeSettings: ThemeSettings = ThemeSettings(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 计算是否深色
    val isDark = when (themeSettings.darkMode) {
        DarkMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkMode.LIGHT -> false
        DarkMode.DARK -> true
    }

    // PureBlack 模式下用纯黑而非深灰
    val pureBlackEnabled = themeSettings.pureBlack && isDark

    // MD3E 调色板风格映射（目前通过 AndroidX dynamic color 实现）
    val colorScheme = when {
        themeSettings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val scheme = if (isDark) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            // PureBlack 覆盖 — 覆盖所有背景/surface 为纯黑，文字为白色
            if (pureBlackEnabled) {
                val black = Color.Black
                val white = Color.White
                val gray = Color(0xFF9E9E9E)
                val darkGray = Color(0xFF424242)
                scheme.copy(
                    background = black,
                    onBackground = white,
                    surface = black,
                    onSurface = white,
                    surfaceVariant = black,
                    onSurfaceVariant = gray,
                    surfaceContainerLowest = black,
                    surfaceContainerLow = black,
                    surfaceContainer = black,
                    surfaceContainerHigh = black,
                    surfaceContainerHighest = black,
                    outline = darkGray,
                    outlineVariant = darkGray,
                    inverseSurface = white,
                    inverseOnSurface = black,
                    scrim = black
                )
            } else {
                scheme
            }
        }
        isDark -> {
            darkColorScheme().let { scheme ->
                if (pureBlackEnabled) {
                    val black = Color.Black
                    val white = Color.White
                    val gray = Color(0xFF9E9E9E)
                    val darkGray = Color(0xFF424242)
                    scheme.copy(
                        background = black,
                        onBackground = white,
                        surface = black,
                        onSurface = white,
                        surfaceVariant = black,
                        onSurfaceVariant = gray,
                        surfaceContainerLowest = black,
                        surfaceContainerLow = black,
                        surfaceContainer = black,
                        surfaceContainerHigh = black,
                        surfaceContainerHighest = black,
                        outline = darkGray,
                        outlineVariant = darkGray,
                        inverseSurface = white,
                        inverseOnSurface = black,
                        scrim = black
                    )
                } else {
                    scheme
                }
            }
        }
        else -> lightColorScheme()
    }

    // MD3E 扩展颜色信息
    val md3eColors = MD3EColors(
        contrastLevel = themeSettings.contrastLevel,
        paletteStyle = themeSettings.paletteStyle,
        pureBlack = themeSettings.pureBlack
    )

    CompositionLocalProvider(LocalMD3EColors provides md3eColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

/**
 * 获取当前 MD3E 颜色配置
 */
@Composable
fun currentMD3EColors(): MD3EColors = LocalMD3EColors.current
