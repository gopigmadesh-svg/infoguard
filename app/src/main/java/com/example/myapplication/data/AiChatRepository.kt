package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.NetworkConfig
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.AiChatHistoryItem
import com.example.myapplication.data.network.model.AiChatRequest
import com.example.myapplication.data.network.model.AiChatResponseData
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AI Providers supported by TruthLens
 */
enum class AiProvider {
    GEMINI,
    OPENAI
}

/**
 * AI Models configurable in Ask AI Chat Screen
 */
enum class AiModelOption(
    val id: String,
    val displayName: String,
    val provider: String,
    val modelCode: String,
    val badge: String,
    val description: String
) {
    GEMINI_15_FLASH(
        id = "gemini-1.5-flash",
        displayName = "Gemini 1.5 Flash",
        provider = "gemini",
        modelCode = "gemini-1.5-flash",
        badge = "Recommended",
        description = "High-speed, fact-checking and misinformation verification"
    ),
    GEMINI_20_FLASH(
        id = "gemini-2.0-flash",
        displayName = "Gemini 2.0 Flash",
        provider = "gemini",
        modelCode = "gemini-2.0-flash",
        badge = "Next-Gen",
        description = "Advanced reasoning, cross-source synthesis & bias analysis"
    ),
    OPENAI_GPT4O_MINI(
        id = "gpt-4o-mini",
        displayName = "GPT-4o Mini",
        provider = "openai",
        modelCode = "gpt-4o-mini",
        badge = "OpenAI",
        description = "Lightweight OpenAI intelligence for digital claim verification"
    );

    companion object {
        fun fromId(id: String): AiModelOption {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GEMINI_15_FLASH
        }
    }
}

/**
 * Fact-Checking Focus Modes
 */
enum class AiVerificationFocus(
    val id: String,
    val title: String,
    val description: String,
    val promptContext: String
) {
    FACT_CHECK(
        id = "fact_check",
        title = "Fact-Checking",
        description = "Verify factual claims against authoritative databases",
        promptContext = "[Focus: Verify factual accuracy and identify disinformation patterns]"
    ),
    SOURCE_BIAS(
        id = "source_bias",
        title = "Source & Bias",
        description = "Analyze publisher bias, tone, and editorial integrity",
        promptContext = "[Focus: Analyze source credibility, potential bias, and journalistic standards]"
    ),
    DEEP_EVIDENCE(
        id = "deep_evidence",
        title = "Deep Evidence",
        description = "In-depth breakdown with primary source citations",
        promptContext = "[Focus: Provide detailed historical context, primary documents, and counter-evidence]"
    );

    companion object {
        fun fromId(id: String): AiVerificationFocus {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FACT_CHECK
        }
    }
}

data class ChatHistoryMessage(
    val text: String,
    val isUser: Boolean
)

/**
 * Secure AI Chat Repository for TruthLens.
 * 
 * SECURITY ARCHITECTURE:
 * - API keys are NEVER handled on the Android client.
 * - All AI requests are securely proxied through the TruthLens PHP backend (ai_chat.php).
 * - Backend server communicates with Gemini / OpenAI using server-side configuration.
 */
object AiChatRepository {

    private const val PREFS_NAME = "truthlens_ai_config"
    private const val KEY_SELECTED_MODEL = "selected_ai_model"
    private const val KEY_SELECTED_FOCUS = "selected_ai_focus"

    // User-configurable preferences
    var selectedModel: AiModelOption = AiModelOption.GEMINI_15_FLASH
    var selectedFocus: AiVerificationFocus = AiVerificationFocus.FACT_CHECK

    // Backward-compatible properties
    var apiKey: String = ""
    var selectedProvider: AiProvider
        get() = if (selectedModel.provider == "openai") AiProvider.OPENAI else AiProvider.GEMINI
        set(value) {
            selectedModel = if (value == AiProvider.OPENAI) AiModelOption.OPENAI_GPT4O_MINI else AiModelOption.GEMINI_15_FLASH
        }
    var customBackendUrl: String = ""

    fun loadPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedModelId = prefs.getString(KEY_SELECTED_MODEL, AiModelOption.GEMINI_15_FLASH.id) ?: AiModelOption.GEMINI_15_FLASH.id
        val savedFocusId = prefs.getString(KEY_SELECTED_FOCUS, AiVerificationFocus.FACT_CHECK.id) ?: AiVerificationFocus.FACT_CHECK.id
        selectedModel = AiModelOption.fromId(savedModelId)
        selectedFocus = AiVerificationFocus.fromId(savedFocusId)
    }

    fun savePreferences(context: Context, model: AiModelOption, focus: AiVerificationFocus) {
        selectedModel = model
        selectedFocus = focus
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_SELECTED_MODEL, model.id)
            putString(KEY_SELECTED_FOCUS, focus.id)
            apply()
        }
    }

    /**
     * Sends message to TruthLens secure backend AI service.
     * Never returns a fake AI response when the server is unreachable;
     * returns a clear, actionable connection error instead.
     */
    suspend fun fetchAiResponse(
        history: List<ChatHistoryMessage>,
        userMessage: String,
        userId: Int? = null
    ): String = withContext(Dispatchers.IO) {
        val trimmedMessage = userMessage.trim()
        if (trimmedMessage.isBlank()) {
            return@withContext "Please type a message before sending."
        }

        // Map recent history turns (last 8)
        val historyItems = history.takeLast(8).map {
            AiChatHistoryItem(
                role = if (it.isUser) "user" else "model",
                text = it.text
            )
        }

        // Formulate prompt with verification focus context
        val contextPrompt = if (selectedFocus != AiVerificationFocus.FACT_CHECK) {
            "${selectedFocus.promptContext}\n\n$trimmedMessage"
        } else {
            trimmedMessage
        }

        val request = AiChatRequest(
            prompt = contextPrompt,
            history = historyItems,
            provider = selectedModel.provider,
            model = selectedModel.modelCode,
            userId = userId
        )

        // 1. Primary: Try Retrofit API Service
        try {
            val response = RetrofitClient.apiService.sendAiChatMessage(request)
            if (response.success) {
                val reply = response.data?.reply ?: response.message
                if (reply.isNotBlank()) {
                    return@withContext reply
                }
            }
        } catch (_: Exception) {
            // Retrofit failed, try resilient DirectApiClient fallback
        }

        // 2. Secondary: Try Direct OkHttp Client Fallback with ApiResponse<AiChatResponseData>
        try {
            val directResponse = DirectApiClient.postJson<AiChatResponseData>("ai_chat.php", request)
            if (directResponse.success && directResponse.data != null) {
                val directReply = directResponse.data.reply
                if (directReply.isNotBlank()) {
                    return@withContext directReply
                }
            }
        } catch (_: Exception) {
            // Primary URL failed
        }

        // 3. Fallback: If primary URL failed, try alternative endpoints if available
        val currentBase = NetworkConfig.currentBaseUrl
        val alternateBaseUrls = mutableListOf<String>()

        if (!currentBase.contains("127.0.0.1")) {
            alternateBaseUrls.add(NetworkConfig.DEFAULT_USB_BASE_URL)
        }
        if (!currentBase.contains("172.23.51.191")) {
            alternateBaseUrls.add(NetworkConfig.DEFAULT_LAN_BASE_URL)
        }
        if (NetworkConfig.isEmulator && !currentBase.contains("10.0.2.2")) {
            alternateBaseUrls.add(NetworkConfig.DEFAULT_EMULATOR_BASE_URL)
        }

        for (altBase in alternateBaseUrls) {
            try {
                val directType = object : com.google.gson.reflect.TypeToken<com.example.myapplication.data.network.model.ApiResponse<AiChatResponseData>>() {}.type
                val url = if (altBase.endsWith("/")) "${altBase}ai_chat.php" else "$altBase/ai_chat.php"
                val jsonPayload = RetrofitClient.gson.toJson(request)
                val requestBody = jsonPayload.toRequestBody(DirectApiClient.JSON_MEDIA_TYPE)
                val okReq = okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("X-Tunnel-Skip-Anti-Abuse", "true")
                    .build()

                RetrofitClient.okHttpClient.newCall(okReq).execute().use { resp ->
                    val body = resp.body?.string().orEmpty()
                    if (resp.isSuccessful && body.isNotBlank()) {
                        val parsed = RetrofitClient.gson.fromJson<com.example.myapplication.data.network.model.ApiResponse<AiChatResponseData>>(body, directType)
                        if (parsed.success && parsed.data != null && parsed.data.reply.isNotBlank()) {
                            // Automatically update working base URL
                            NetworkConfig.setCustomBaseUrl(altBase)
                            return@withContext parsed.data.reply
                        }
                    }
                }
            } catch (_: Exception) {
                // Try next alternate URL
            }
        }

        // 4. Server Unreachable: Display clear, actionable error (no fake AI response)
        val serverTarget = NetworkConfig.currentBaseUrl
        return@withContext "⚠️ **TruthLens AI Server Connection Error**\n\n" +
                "Could not establish a connection to the TruthLens backend server at:\n`$serverTarget`\n\n" +
                "**Troubleshooting Checklist:**\n" +
                "1. **Apache Server:** Verify that Apache is started in your XAMPP Control Panel on port 80.\n" +
                "2. **USB Debugging (Physical Device):** Run `ensure_adb_reverse.bat` on your PC so port 80 is forwarded to your phone (`adb reverse tcp:80 tcp:80`).\n" +
                "3. **Wi-Fi LAN (Same Network):** Ensure your phone and PC are connected to the same Wi-Fi.\n" +
                "4. Tap ⚙️ at the top right of this chat to verify the server address or select an AI model."
    }
}
