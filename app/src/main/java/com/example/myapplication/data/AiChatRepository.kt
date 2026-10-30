package com.example.myapplication.data

import com.example.myapplication.data.network.client.NetworkConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

enum class AiProvider {
    GEMINI,
    OPENAI
}

data class ChatHistoryMessage(
    val text: String,
    val isUser: Boolean
)

object AiChatRepository {

    var apiKey: String = ""
    var selectedProvider: AiProvider = AiProvider.GEMINI
    var customBackendUrl: String = ""

    private const val SYSTEM_PROMPT =
        "You are TruthLens AI Assistant, an expert AI specialized in detecting fake news, misinformation, verifying sources, evaluating claims, and explaining bias. Provide clear, objective, concise, and helpful advice to guide users in evaluating digital content."

    suspend fun fetchAiResponse(
        history: List<ChatHistoryMessage>,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        if (userMessage.isBlank()) {
            return@withContext "Please type a message before sending."
        }

        val trimmedKey = apiKey.trim()
        val trimmedUrl = if (customBackendUrl.isNotBlank()) {
            customBackendUrl.trim()
        } else {
            "${NetworkConfig.currentBaseUrl}ai_chat.php"
        }

        // 1. Try Gemini API if key is provided
        if (trimmedKey.isNotEmpty() && selectedProvider == AiProvider.GEMINI) {
            try {
                return@withContext callGeminiApi(trimmedKey, history, userMessage)
            } catch (e: Exception) {
                return@withContext "Gemini API Request Failed: ${e.localizedMessage ?: e.message}. Please verify your Gemini API key in Chat Config (⚙️)."
            }
        }

        // 2. Try OpenAI API if key is provided
        if (trimmedKey.isNotEmpty() && selectedProvider == AiProvider.OPENAI) {
            try {
                return@withContext callOpenAiApi(trimmedKey, history, userMessage)
            } catch (e: Exception) {
                return@withContext "OpenAI API Request Failed: ${e.localizedMessage ?: e.message}. Please verify your OpenAI API key in Chat Config (⚙️)."
            }
        }

        // 3. Try Backend AI Endpoint (http://127.0.0.1/Truthlense_backened/ai_chat.php)
        try {
            return@withContext callCustomBackend(trimmedUrl, history, userMessage)
        } catch (e: Exception) {
            // 4. Fallback Fact-Check Guidance if backend endpoint is unreachable
            return@withContext generateFallbackFactCheckGuidance(userMessage)
        }
    }

    private fun callGeminiApi(
        key: String,
        history: List<ChatHistoryMessage>,
        userMessage: String
    ): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$key"
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.doOutput = true

        val rootJson = JSONObject()

        // System Instruction
        val sysInstruction = JSONObject()
        val sysParts = JSONArray().put(JSONObject().put("text", SYSTEM_PROMPT))
        sysInstruction.put("parts", sysParts)
        rootJson.put("system_instruction", sysInstruction)

        // Contents
        val contentsArray = JSONArray()
        for (msg in history.takeLast(10)) {
            val contentObj = JSONObject()
            contentObj.put("role", if (msg.isUser) "user" else "model")
            val parts = JSONArray().put(JSONObject().put("text", msg.text))
            contentObj.put("parts", parts)
            contentsArray.put(contentObj)
        }

        // Current User Message
        val currentObj = JSONObject()
        currentObj.put("role", "user")
        val currentParts = JSONArray().put(JSONObject().put("text", userMessage))
        currentObj.put("parts", currentParts)
        contentsArray.put(currentObj)

        rootJson.put("contents", contentsArray)

        val writer = OutputStreamWriter(conn.outputStream, "UTF-8")
        writer.write(rootJson.toString())
        writer.flush()
        writer.close()

        val responseCode = conn.responseCode
        if (responseCode in 200..299) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()

            val resJson = JSONObject(sb.toString())
            val candidates = resJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "No text generated.")
                    }
                }
            }
            return "Empty response returned from Gemini API."
        } else {
            val errorStream = conn.errorStream
            val errorText = if (errorStream != null) {
                BufferedReader(InputStreamReader(errorStream, "UTF-8")).use { it.readText() }
            } else "HTTP $responseCode"
            throw Exception("Gemini Error (HTTP $responseCode): $errorText")
        }
    }

    private fun callOpenAiApi(
        key: String,
        history: List<ChatHistoryMessage>,
        userMessage: String
    ): String {
        val url = URL("https://api.openai.com/v1/chat/completions")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.setRequestProperty("Authorization", "Bearer $key")
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.doOutput = true

        val rootJson = JSONObject()
        rootJson.put("model", "gpt-4o-mini")

        val messagesArray = JSONArray()

        // System Prompt
        messagesArray.put(JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))

        // History
        for (msg in history.takeLast(10)) {
            val msgObj = JSONObject()
            msgObj.put("role", if (msg.isUser) "user" else "assistant")
            msgObj.put("content", msg.text)
            messagesArray.put(msgObj)
        }

        // Current User Message
        messagesArray.put(JSONObject().put("role", "user").put("content", userMessage))
        rootJson.put("messages", messagesArray)

        val writer = OutputStreamWriter(conn.outputStream, "UTF-8")
        writer.write(rootJson.toString())
        writer.flush()
        writer.close()

        val responseCode = conn.responseCode
        if (responseCode in 200..299) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()

            val resJson = JSONObject(sb.toString())
            val choices = resJson.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val messageObj = choices.getJSONObject(0).optJSONObject("message")
                if (messageObj != null) {
                    return messageObj.optString("content", "No response generated.")
                }
            }
            return "Empty response returned from OpenAI API."
        } else {
            val errorStream = conn.errorStream
            val errorText = if (errorStream != null) {
                BufferedReader(InputStreamReader(errorStream, "UTF-8")).use { it.readText() }
            } else "HTTP $responseCode"
            throw Exception("OpenAI Error (HTTP $responseCode): $errorText")
        }
    }

    private fun callCustomBackend(
        backendUrl: String,
        history: List<ChatHistoryMessage>,
        userMessage: String
    ): String {
        val url = URL(backendUrl)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.doOutput = true

        val rootJson = JSONObject()
        rootJson.put("prompt", userMessage)

        val historyArray = JSONArray()
        for (msg in history) {
            val h = JSONObject()
            h.put("role", if (msg.isUser) "user" else "assistant")
            h.put("text", msg.text)
            historyArray.put(h)
        }
        rootJson.put("history", historyArray)

        val writer = OutputStreamWriter(conn.outputStream, "UTF-8")
        writer.write(rootJson.toString())
        writer.flush()
        writer.close()

        val responseCode = conn.responseCode
        if (responseCode in 200..299) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val text = reader.readText()
            reader.close()
            return try {
                val json = JSONObject(text)
                json.optString("reply", json.optString("response", text))
            } catch (_: Exception) {
                text
            }
        } else {
            throw Exception("Custom Backend HTTP $responseCode Error")
        }
    }

    private fun generateFallbackFactCheckGuidance(query: String): String {
        val lower = query.lowercase()
        val configNote = "\n\n💡 *AI Setup Note: To enable live model responses (Gemini / OpenAI), tap the ⚙️ Config icon at the top right of this chat.*"

        return when {
            lower.contains("health") || lower.contains("cure") || lower.contains("vitamin") || lower.contains("medical") || lower.contains("vaccine") -> {
                "🏥 **Health Claim Verification Guide:**\n\n1. **Check Primary Clinical Repositories:** Verify claims against PubMed, NIH, CDC, or WHO databases rather than social posts.\n2. **Look for Randomized Controlled Trials (RCTs):** Individual anecdotes do not prove medical efficacy.\n3. **Watch for Sensational Buzzwords:** Terms like 'miracle cure' or 'secret remedy' are classic red flags.$configNote"
            }
            lower.contains("election") || lower.contains("politics") || lower.contains("vote") || lower.contains("government") -> {
                "🏛️ **Political & Election Claim Guide:**\n\n1. **Check Official Electoral Bodies:** Cross-examine voting law changes directly with local election commissions.\n2. **Verify Video Timestamps:** Viral videos are frequently recycled from past years or different regions.\n3. **Consult Non-Partisan Aggregators:** Review FactCheck.org, PolitiFact, or Reuters Fact Check.$configNote"
            }
            lower.contains("image") || lower.contains("photo") || lower.contains("deepfake") || lower.contains("video") -> {
                "🖼️ **Media & Deepfake Detection Guide:**\n\n1. **Inspect Lighting & Reflections:** AI generated images often show inconsistent shadows, mismatched ear shapes, or blurred hair outlines.\n2. **Run Reverse Image Search:** Search the image on Google Images or TinEye to find the earliest date it was posted online.\n3. **Examine Metadata:** Authentic news photos preserve EXIF camera metadata.$configNote"
            }
            lower.contains("how") || lower.contains("spot") || lower.contains("fake") || lower.contains("identify") -> {
                "🔍 **5 Golden Rules for Fact-Checking:**\n\n1. **Examine the Domain URL:** Watch for spoofed domain names (e.g. .co or slight misspellings).\n2. **Check the Author Bio:** Legitimate journalism includes reporter disclosures and staff contacts.\n3. **Cross-Search 3 Independent Outlets:** If major news agencies aren't reporting it, treat it with caution.\n4. **Read Beyond the Headline:** Clickbait headlines often misrepresent the actual body text.\n5. **Pause Before Sharing:** Emotional triggers are designed to induce rapid sharing.$configNote"
            }
            else -> {
                "✨ **TruthLens Fact-Check Guidance:**\n\nTo verify: \"$query\"\n\n1. **Identify the Core Claim:** Is it a factual statistic or an opinion?\n2. **Source Inspection:** Who is the original publisher, and do they cite primary documentation?\n3. **Cross-Check Verification:** Have independent outlets or fact-checkers confirmed this item?$configNote"
            }
        }
    }
}
