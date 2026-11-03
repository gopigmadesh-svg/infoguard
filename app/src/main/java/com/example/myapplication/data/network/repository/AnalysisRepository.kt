package com.example.myapplication.data.network.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.NetworkConfig
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.model.AnalyzeClaimRequest
import com.example.myapplication.data.network.model.AnalyzeImageRequest
import com.example.myapplication.data.network.model.AnalyzeUrlRequest
import com.example.myapplication.data.network.model.RecentScanDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for claim analysis, image forensics, and verification scan history.
 */
class AnalysisRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun analyzeClaim(
        userId: Int?,
        content: String,
        scanType: String = "news"
    ): Result<AnalysisResultDto> = withContext(Dispatchers.IO) {
        try {
            val request = AnalyzeClaimRequest(userId = userId, content = content, scanType = scanType)
            val response = try {
                apiService.analyzeClaim(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<AnalysisResultDto>("analysis/analyze_claim.php", request)
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

    suspend fun analyzeUrl(
        userId: Int?,
        url: String
    ): Result<AnalysisResultDto> = withContext(Dispatchers.IO) {
        try {
            val request = AnalyzeUrlRequest(userId = userId, url = url, scanType = "url")
            val response = try {
                apiService.analyzeUrl(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<AnalysisResultDto>("analysis/analyze_url.php", request)
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

    suspend fun analyzeImage(
        userId: Int?,
        imageBase64: String,
        mimeType: String = "image/jpeg",
        claim: String? = null,
        ocrText: String? = null
    ): Result<AnalysisResultDto> = withContext(Dispatchers.IO) {
        try {
            val request = AnalyzeImageRequest(
                userId = userId,
                image = imageBase64,
                mimeType = mimeType,
                claim = claim,
                ocrText = ocrText
            )
            val response = try {
                apiService.analyzeImage(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<AnalysisResultDto>("analysis/analyze_image.php", request)
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

    suspend fun analyzeImageUri(
        context: Context,
        userId: Int?,
        imageUri: Uri,
        claim: String? = null,
        ocrText: String? = null
    ): Result<AnalysisResultDto> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            val imageBytes = contentResolver.openInputStream(imageUri)?.use { input ->
                input.readBytes()
            } ?: return@withContext Result.failure(Exception("Unable to read selected image data"))

            val base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            analyzeImage(
                userId = userId,
                imageBase64 = base64String,
                mimeType = mimeType,
                claim = claim,
                ocrText = ocrText
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentScans(
        userId: Int,
        limit: Int = 10,
        token: String? = null
    ): Result<List<RecentScanDto>> = withContext(Dispatchers.IO) {
        try {
            val resolvedToken = token ?: NetworkConfig.currentAuthToken
            val response = try {
                apiService.getRecentScans(userId, limit, resolvedToken)
            } catch (e: Exception) {
                val params = mutableMapOf(
                    "user_id" to userId.toString(),
                    "limit" to limit.toString()
                )
                if (!resolvedToken.isNullOrEmpty()) {
                    params["token"] = resolvedToken
                }
                DirectApiClient.getJson<List<RecentScanDto>>(
                    "analysis/get_recent_scans.php",
                    params
                )
            }

            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearHistory(
        userId: Int,
        token: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resolvedToken = token ?: NetworkConfig.currentAuthToken
            val body = mutableMapOf<String, Any>("user_id" to userId)
            if (!resolvedToken.isNullOrEmpty()) {
                body["token"] = resolvedToken
            }
            val response = try {
                apiService.clearHistory(body)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("analysis/clear_history.php", body)
            }

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
