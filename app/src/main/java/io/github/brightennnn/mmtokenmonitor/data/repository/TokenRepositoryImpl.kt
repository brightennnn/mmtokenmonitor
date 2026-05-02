package io.github.brightennnn.mmtokenmonitor.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.brightennnn.mmtokenmonitor.data.remote.MiniMaxApi
import io.github.brightennnn.mmtokenmonitor.domain.model.ModelQuota
import io.github.brightennnn.mmtokenmonitor.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "minimax_token_prefs")

@Singleton
class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: MiniMaxApi
) : TokenRepository {

    private object PrefsKeys {
        val API_KEY = stringPreferencesKey("api_key")
    }

    override val apiKey: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[PrefsKeys.API_KEY] }

    override suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.API_KEY] = key
        }
    }

    override suspend fun clearApiKey() {
        context.dataStore.edit { prefs ->
            prefs.remove(PrefsKeys.API_KEY)
        }
    }

    override suspend fun fetchQuotas(apiKey: String): Result<List<ModelQuota>> {
        return try {
            val response = api.getTokenPlan("Bearer $apiKey")
            val quotas = response.modelRemains.map { dto ->
                ModelQuota(
                    modelName = dto.modelName,
                    intervalTotal = dto.currentIntervalTotalCount,
                    intervalUsed = dto.currentIntervalUsageCount,
                    weeklyTotal = dto.currentWeeklyTotalCount,
                    weeklyUsed = dto.currentWeeklyUsageCount,
                    intervalStartMs = dto.startTime,
                    intervalEndMs = dto.endTime,
                    weeklyStartMs = dto.weeklyStartTime,
                    weeklyEndMs = dto.weeklyEndTime
                )
            }
            Result.success(quotas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
