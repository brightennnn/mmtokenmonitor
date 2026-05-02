package io.github.brightennnn.mmtokenmonitor.domain.model

/**
 * MD3E Palette Style — 9种调色板风格
 * @param id Int 对应 AndroidX CorePalette 的色调映射
 * @param displayName String 显示名称
 */
enum class PaletteStyle(val id: Int, val displayName: String) {
    TonalSpot(1, "TonalSpot"),      // Material3 默认风格（中性色调）
    Neutral(2, "Neutral"),           // 柔和中性色调
    Vibrant(3, "Vibrant"),           // 鲜艳活泼色调
    Expressive(4, "Expressive"),    // MD3E 专属：表达性色调（更饱和、更具表现力）
    Rainbow(5, "Rainbow"),          // 彩虹色
    FruitSalad(6, "FruitSalad"),    // 水果沙拉色
    Monochrome(7, "Monochrome"),    // 单色灰阶
    Fidelity(8, "Fidelity"),        // 忠实原色（图片取色）
    Content(9, "Content");          // Content 动态色调

    companion object {
        fun fromId(id: Int): PaletteStyle =
            entries.find { it.id == id } ?: TonalSpot
    }
}
