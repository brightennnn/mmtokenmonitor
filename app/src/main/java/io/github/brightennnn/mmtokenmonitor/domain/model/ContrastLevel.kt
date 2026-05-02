package io.github.brightennnn.mmtokenmonitor.domain.model

/**
 * MD3E Contrast Level — 5级对比度
 * 影响 Material3 tonalElevation 和 surface tint 的对比强度
 * @param value Float 偏移值，范围 [-1f, 1f]
 */
enum class ContrastLevel(val value: Float) {
    VeryLow(-1.0f),   // 极低对比度
    Low(-0.5f),        // 低对比度
    Default(0.0f),     // 默认对比度
    Medium(0.5f),      // 中等对比度
    High(1.0f);        // 高对比度

    companion object {
        fun fromOrdinal(ordinal: Int): ContrastLevel =
            entries.getOrElse(ordinal) { Default }
    }
}
