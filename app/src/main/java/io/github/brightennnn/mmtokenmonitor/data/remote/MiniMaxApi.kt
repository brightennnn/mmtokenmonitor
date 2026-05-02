package io.github.brightennnn.mmtokenmonitor.data.remote

import retrofit2.http.GET
import retrofit2.http.Header

interface MiniMaxApi {
    @GET("v1/token_plan/remains")
    suspend fun getTokenPlan(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): TokenPlanResponse
}
