package com.example.myapplication.data.network.client

import com.example.myapplication.data.network.model.ApiResponse
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.reflect.Type

/**
 * Direct OkHttp client executing type-safe calls with Gson parsing.
 * Provides resilient, standalone execution matching all backend endpoints.
 */
object DirectApiClient {

    @PublishedApi
    internal val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun <T> postJsonWithType(
        endpoint: String,
        bodyObject: Any?,
        responseType: Type
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        val url = resolveUrl(endpoint)
        val jsonPayload = if (bodyObject != null) RetrofitClient.gson.toJson(bodyObject) else "{}"
        val requestBody = jsonPayload.toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        executeRequestWithType<T>(request, responseType)
    }

    suspend inline fun <reified T> postJson(
        endpoint: String,
        bodyObject: Any?
    ): ApiResponse<T> {
        val type = object : TypeToken<ApiResponse<T>>() {}.type
        return postJsonWithType(endpoint, bodyObject, type)
    }

    suspend fun <T> getJsonWithType(
        endpoint: String,
        queryParams: Map<String, String>,
        responseType: Type
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        val baseUrl = resolveUrl(endpoint)
        val finalUrl = if (queryParams.isNotEmpty()) {
            val queryString = queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
            if (baseUrl.contains("?")) "$baseUrl&$queryString" else "$baseUrl?$queryString"
        } else {
            baseUrl
        }

        val request = Request.Builder()
            .url(finalUrl)
            .get()
            .build()

        executeRequestWithType<T>(request, responseType)
    }

    suspend inline fun <reified T> getJson(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap()
    ): ApiResponse<T> {
        val type = object : TypeToken<ApiResponse<T>>() {}.type
        return getJsonWithType(endpoint, queryParams, type)
    }

    fun resolveUrl(endpoint: String): String {
        val base = NetworkConfig.currentBaseUrl
        val cleanEndpoint = if (endpoint.startsWith("/")) endpoint.substring(1) else endpoint
        return "$base$cleanEndpoint"
    }

    fun <T> executeRequestWithType(request: Request, responseType: Type): ApiResponse<T> {
        return try {
            RetrofitClient.okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (bodyString.isBlank()) {
                    return ApiResponse(
                        success = response.isSuccessful,
                        message = "Empty response from server (HTTP ${response.code})",
                        data = null
                    )
                }

                try {
                    RetrofitClient.gson.fromJson<ApiResponse<T>>(bodyString, responseType)
                } catch (parseEx: Exception) {
                    ApiResponse(
                        success = false,
                        message = "Parsing failed: ${parseEx.message}. Raw: $bodyString",
                        data = null
                    )
                }
            }
        } catch (ioEx: IOException) {
            val details = ioEx.localizedMessage ?: "Connection error"
            val isConnRefused = details.contains("127.0.0.1") ||
                    details.contains("failed to connect", ignoreCase = true) ||
                    details.contains("refused", ignoreCase = true) ||
                    details.contains("ECONNREFUSED", ignoreCase = true)

            val helpMsg = if (isConnRefused) {
                if (NetworkConfig.isEmulator) {
                    "Cannot reach backend at 10.0.2.2:80. Ensure XAMPP Apache is running on your PC."
                } else {
                    "Cannot connect to backend (connection refused at 127.0.0.1:80).\n• Keep USB connected & run 'ensure_adb_reverse.bat' on your PC (or 'adb reverse tcp:80 tcp:80').\n• Ensure XAMPP Apache is running on port 80."
                }
            } else if (ioEx is java.net.SocketTimeoutException) {
                "Connection timed out. Please ensure XAMPP Apache is responding."
            } else {
                "Network connection failed: $details"
            }
            ApiResponse(
                success = false,
                message = helpMsg,
                data = null
            )
        }
    }

    inline fun <reified T> executeRequest(request: Request): ApiResponse<T> {
        val type = object : TypeToken<ApiResponse<T>>() {}.type
        return executeRequestWithType(request, type)
    }
}
