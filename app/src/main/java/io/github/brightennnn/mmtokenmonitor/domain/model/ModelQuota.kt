package io.github.brightennnn.mmtokenmonitor.domain.model

data class ModelQuota(
    val modelName: String,
    val intervalTotal: Int,
    val intervalUsed: Int,
    val weeklyTotal: Int,
    val weeklyUsed: Int,
    val intervalStartMs: Long,
    val intervalEndMs: Long,
    val weeklyStartMs: Long,
    val weeklyEndMs: Long
)
