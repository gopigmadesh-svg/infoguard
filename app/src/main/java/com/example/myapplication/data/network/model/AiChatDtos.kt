package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Request payload sent to TruthLens PHP backend ai_chat.php
 */
data class AiChatRequest(
    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("history")
    val history: List<AiChatHistoryItem> = emptyList(),

    @SerializedName("provider")
    val provider: String? = "gemini",

    @SerializedName("model")
    val model: String? = "gemini-1.5-flash",

    @SerializedName("user_id")
    val userId: Int? = null
)

/**
 * Context history item representing previous turns in the chat
 */
data class AiChatHistoryItem(
    @SerializedName("role")
    val role: String, // "user" or "model" / "assistant"

    @SerializedName("text")
    val text: String
)

/**
 * Detailed metadata payload returned inside ApiResponse.data
 */
data class AiChatResponseData(
    @SerializedName("reply")
    val reply: String,

    @SerializedName("provider")
    val provider: String? = null,

    @SerializedName("model")
    val model: String? = null,

    @SerializedName("is_live_model")
    val isLiveModel: Boolean = false
)

/**
 * Direct response structure from ai_chat.php supporting both root 'reply' and nested 'data'
 */
data class AiChatDirectResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("reply")
    val reply: String? = null,

    @SerializedName("data")
    val data: AiChatResponseData? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
)

