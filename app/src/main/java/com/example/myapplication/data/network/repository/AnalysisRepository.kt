package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.model.AnalyzeClaimRequest
import com.example.myapplication.data.network.model.RecentScanDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for claim analysis and verification scan history.
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

    suspend fun getRecentScans(
        userId: Int,
        limit: Int = 10
    ): Result<List<RecentScanDto>> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.getRecentScans(userId, limit)
            } catch (e: Exception) {
                DirectApiClient.getJson<List<RecentScanDto>>(
                    "analysis/get_recent_scans.php",
                    mapOf("user_id" to userId.toString(), "limit" to limit.toString())
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
}

