package io.github.brightennnn.mmtokenmonitor.domain.repository

import io.github.brightennnn.mmtokenmonitor.domain.model.ModelQuota
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val apiKey: Flow<String?>
    suspend fun saveApiKey(key: String)
    suspend fun clearApiKey()
    suspend fun fetchQuotas(apiKey: String): Result<List<ModelQuota>>
}
