package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.RemoveArticleRequest
import com.example.myapplication.data.network.model.SaveArticleRequest
import com.example.myapplication.data.network.model.SavedArticleDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing saved fact-checked articles and bookmarks.
 */
class ArticleRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getSavedArticles(userId: Int): Result<List<SavedArticleDto>> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.getSavedArticles(userId)
            } catch (e: Exception) {
                DirectApiClient.getJson<List<SavedArticleDto>>(
                    "articles/get_saved.php",
                    mapOf("user_id" to userId.toString())
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

    suspend fun saveArticle(
        userId: Int,
        title: String,
        summary: String? = null,
        source: String? = null,
        url: String? = null,
        trustScore: Int = 75
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = SaveArticleRequest(
                userId = userId,
                title = title,
                summary = summary,
                source = source,
                url = url,
                trustScore = trustScore
            )
            val response = try {
                apiService.saveArticle(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("articles/save_article.php", request)
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

    suspend fun removeArticle(
        userId: Int,
        articleId: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = RemoveArticleRequest(userId = userId, articleId = articleId)
            val response = try {
                apiService.removeSavedArticle(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("articles/remove_saved.php", request)
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

