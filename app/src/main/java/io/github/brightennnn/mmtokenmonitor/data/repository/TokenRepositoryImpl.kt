package io.github.brightennnn.mmtokenmonitor.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.brightennnn.mmtokenmonitor.data.remote.MiniMaxApi
import io.github.brightennnn.mmtokenmonitor.domain.model.ApiKeyProfile
import io.github.brightennnn.mmtokenmonitor.domain.model.ModelQuota
import io.github.brightennnn.mmtokenmonitor.domain.repository.TokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "minimax_token_prefs")

@Singleton
class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: MiniMaxApi
) : TokenRepository {

    private object PrefsKeys {
        val API_KEY_PROFILES = stringPreferencesKey("api_key_profiles")
        val ACTIVE_API_KEY_ID = stringPreferencesKey("active_api_key_id")
    }

    override val apiKeyProfiles: Flow<List<ApiKeyProfile>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]"
            parseProfiles(json)
        }

    override val activeApiKey: Flow<String?> = context.dataStore.data
        .map { prefs ->
            val profiles = parseProfiles(prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]")
            val activeId = prefs[PrefsKeys.ACTIVE_API_KEY_ID]
            profiles.find { it.id == activeId }?.key
        }

    private fun parseProfiles(json: String): List<ApiKeyProfile> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ApiKeyProfile(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    key = obj.getString("key"),
                    isActive = obj.getBoolean("isActive")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeProfiles(profiles: List<ApiKeyProfile>): String {
        val array = JSONArray()
        profiles.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("key", p.key)
                put("isActive", p.isActive)
            }
            array.put(obj)
        }
        return array.toString()
    }

    override suspend fun saveApiKey(key: String, name: String) {
        context.dataStore.edit { prefs ->
            val profiles = parseProfiles(prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]").toMutableList()
            // 新 Key 默认不激活
            profiles.add(ApiKeyProfile(name = name, key = key.trim()))
            prefs[PrefsKeys.API_KEY_PROFILES] = serializeProfiles(profiles)
        }
    }

    override suspend fun updateApiKeyProfile(profile: ApiKeyProfile) {
        context.dataStore.edit { prefs ->
            val profiles = parseProfiles(prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]").toMutableList()
            val index = profiles.indexOfFirst { it.id == profile.id }
            if (index >= 0) {
                profiles[index] = profile
                prefs[PrefsKeys.API_KEY_PROFILES] = serializeProfiles(profiles)
            }
        }
    }

    override suspend fun deleteApiKeyProfile(id: String) {
        context.dataStore.edit { prefs ->
            val profiles = parseProfiles(prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]").toMutableList()
            profiles.removeAll { it.id == id }
            // 如果删除的是激活的，清除激活状态
            val activeId = prefs[PrefsKeys.ACTIVE_API_KEY_ID]
            if (activeId == id) {
                prefs.remove(PrefsKeys.ACTIVE_API_KEY_ID)
            }
            prefs[PrefsKeys.API_KEY_PROFILES] = serializeProfiles(profiles)
        }
    }

    override suspend fun setActiveApiKey(id: String) {
        context.dataStore.edit { prefs ->
            val profiles = parseProfiles(prefs[PrefsKeys.API_KEY_PROFILES] ?: "[]").toMutableList()
            profiles.forEachIndexed { index, profile ->
                profiles[index] = profile.copy(isActive = profile.id == id)
            }
            prefs[PrefsKeys.API_KEY_PROFILES] = serializeProfiles(profiles)
            prefs[PrefsKeys.ACTIVE_API_KEY_ID] = id
        }
    }

    override suspend fun clearAllApiKeys() {
        context.dataStore.edit { prefs ->
            prefs.remove(PrefsKeys.API_KEY_PROFILES)
            prefs.remove(PrefsKeys.ACTIVE_API_KEY_ID)
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
