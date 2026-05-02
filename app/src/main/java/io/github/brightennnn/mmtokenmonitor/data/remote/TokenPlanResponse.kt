package io.github.brightennnn.mmtokenmonitor.data.remote

import com.google.gson.annotations.SerializedName

data class TokenPlanResponse(
    @SerializedName("model_remains")
    val modelRemains: List<ModelRemainDto>,
    @SerializedName("base_resp")
    val baseResp: BaseRespDto?
)

data class ModelRemainDto(
    @SerializedName("model_name")
    val modelName: String,
    @SerializedName("current_interval_total_count")
    val currentIntervalTotalCount: Int,
    @SerializedName("current_interval_usage_count")
    val currentIntervalUsageCount: Int,
    @SerializedName("current_weekly_total_count")
    val currentWeeklyTotalCount: Int,
    @SerializedName("current_weekly_usage_count")
    val currentWeeklyUsageCount: Int,
    @SerializedName("start_time")
    val startTime: Long,
    @SerializedName("end_time")
    val endTime: Long,
    @SerializedName("weekly_start_time")
    val weeklyStartTime: Long,
    @SerializedName("weekly_end_time")
    val weeklyEndTime: Long
)

data class BaseRespDto(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("status_msg")
    val statusMsg: String
)
