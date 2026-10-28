package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.UpdateSettingsRequest
import com.example.myapplication.data.network.model.UserSettingsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing application theme, preferences, and notification settings.
 */
class SettingsRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getSettings(userId: Int): Result<UserSettingsDto> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.getSettings(userId)
            } catch (e: Exception) {
                DirectApiClient.getJson<UserSettingsDto>(
                    "settings/get_settings.php",
                    mapOf("user_id" to userId.toString())
                )
            }

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSettings(
        userId: Int,
        theme: String? = null,
        notificationsEnabled: Boolean? = null,
        language: String? = null,
        interests: List<String>? = null
    ): Result<UserSettingsDto> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateSettingsRequest(
                userId = userId,
                theme = theme,
                notificationsEnabled = notificationsEnabled,
                language = language,
                interests = interests
            )
            val response = try {
                apiService.updateSettings(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<UserSettingsDto>("settings/update_settings.php", request)
            }

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

