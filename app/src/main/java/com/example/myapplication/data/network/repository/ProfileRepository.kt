package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.UpdateProfileRequest
import com.example.myapplication.data.network.model.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing user profile information.
 */
class ProfileRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getProfile(userId: Int): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.getProfile(userId)
            } catch (e: Exception) {
                DirectApiClient.getJson<UserDto>(
                    "profile/get_profile.php",
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

    suspend fun updateProfile(
        userId: Int,
        name: String?,
        bio: String? = null,
        avatarUrl: String? = null
    ): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(
                userId = userId,
                name = name,
                bio = bio,
                avatarUrl = avatarUrl
            )
            val response = try {
                apiService.updateProfile(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<UserDto>("profile/update_profile.php", request)
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

