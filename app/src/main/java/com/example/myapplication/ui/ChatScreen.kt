package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.data.AiChatRepository
import com.example.myapplication.data.AiModelOption
import com.example.myapplication.data.AiVerificationFocus
import com.example.myapplication.data.network.client.NetworkConfig
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.AiProvider
import com.example.myapplication.data.ChatHistoryMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        AiChatRepository.loadPreferences(context)
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hi! I'm TruthLens AI, your fact-checking assistant. How can I help you analyze a headline, verify a source, or spot misleading claims today?",
                isUser = false,
                timestamp = "10:00 AM"
            )
        )
    }

    val suggestedQuestions = listOf(
        "How do I spot fake news?",
        "Why was this claim flagged?",
        "How to verify health claims?",
        "How to detect AI images?"
    )

    fun startNewChat() {
        messages.clear()
        messages.add(
            ChatMessage(
                text = "Fresh conversation started. What news item or claim would you like to verify?",
                isUser = false
            )
        )
    }

    fun sendMessage(msgText: String) {
        val trimmed = msgText.trim()
        if (trimmed.isEmpty() || isTyping) return

        val userMsg = ChatMessage(text = trimmed, isUser = true)
        messages.add(userMsg)
        inputText = ""
        isTyping = true

        coroutineScope.launch {
            // Auto scroll to bottom
            listState.animateScrollToItem(messages.size)

            val history = messages.map { ChatHistoryMessage(it.text, it.isUser) }
            val responseText = AiChatRepository.fetchAiResponse(history, trimmed)

            messages.add(ChatMessage(text = responseText, isUser = false))
            isTyping = false

            listState.animateScrollToItem(messages.size)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkNavyStart, DarkNavyEnd)
                )
            )
    ) {
        // Ambient background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2000E5FF), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = size.width * 0.6f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(36.dp)) }

                // 1. Top Header Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x261E293B),
                                border = BorderStroke(1.dp, Color(0x3300E5FF)),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { onBackClick() }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawBackArrowIcon(color = Color.White, strokeWidth = 3.5f)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "TruthLens AI",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(HighTrustGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Online • ${AiChatRepository.selectedModel.displayName}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Header Actions: New Chat & AI Config
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // New Chat Button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x261E293B),
                                border = BorderStroke(1.dp, Color(0x3300E5FF)),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { startNewChat() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🧹", fontSize = 16.sp)
                                }
                            }

                            // AI Config Settings Button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x261E293B),
                                border = BorderStroke(1.dp, Color(0x3300E5FF)),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { showConfigDialog = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "⚙️", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                // 2. AI Assistant Welcome Banner Card
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0xFF00E5FF), Color(0xFF0F766E))
                                        )
                                    )
                            ) {
                                Text(text = "✨", fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Hi Alex, I'm your verification guide.",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "I can help you understand news, claims, and explore ways to verify them.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // 3. Suggested Questions Chips
                item {
                    Column {
                        Text(
                            text = "SUGGESTED QUESTIONS",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            suggestedQuestions.forEach { question ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                                    modifier = Modifier.clickable { sendMessage(question) }
                                ) {
                                    Text(
                                        text = question,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Chat Messages List
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(message = message)
                }

                // 5. Typing / Loading Indicator
                item {
                    AnimatedVisibility(
                        visible = isTyping,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, CyanAccent, CircleShape)
                            ) {
                                Text(text = "✨", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0x3300E5FF))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = TealBright,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "TruthLens AI is analyzing...",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }
            }

            // 6. Disclaimer & Input Bar Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "AI generated information may be inaccurate. Verify important claims using reliable sources.",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Ask anything about news...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = CircleShape,
                        color = if (inputText.isNotBlank()) TealBright else Color(0xFF1E293B),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(enabled = inputText.isNotBlank() && !isTyping) {
                                sendMessage(inputText)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "➤",
                                color = if (inputText.isNotBlank()) DarkNavyStart else Color(0xFF64748B),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // AI Config Dialog
    if (showConfigDialog) {
        AiConfigDialog(
            onDismiss = { showConfigDialog = false }
        )
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (message.isUser) {
            // User Message Bubble (Gradient Right-Aligned)
            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp),
                color = Color.Unspecified,
                border = BorderStroke(1.dp, Color(0x665EEAD4)),
                modifier = Modifier.fillMaxWidth(0.82f)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.timestamp,
                            color = Color(0xCCFFFFFF),
                            fontSize = 9.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        } else {
            // AI Response Message Bubble (Left-Aligned with Avatar)
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                        .border(1.dp, CyanAccent, CircleShape)
                ) {
                    Text(text = "✨", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.timestamp,
                            color = Color(0xFF64748B),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiConfigDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf(AiChatRepository.selectedModel) }
    var selectedFocus by remember { mutableStateOf(AiChatRepository.selectedFocus) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, CyanAccent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Text(
                    text = "\u2699\uFE0F AI Model Configuration",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "TruthLens fact-checking is powered by server-side AI reasoning. Choose your preferred AI model engine and verification focus below.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // Select AI Model
                Text(
                    text = "SELECT AI MODEL",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiModelOption.entries.forEach { option ->
                        val isSelected = selectedModel == option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0x3300E5FF) else Color(0x1F1E293B),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CyanAccent else Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedModel = option }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = option.displayName,
                                            color = if (isSelected) CyanAccent else Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) TealBright else Color(0x3394A3B8)
                                        ) {
                                            Text(
                                                text = option.badge,
                                                color = if (isSelected) DarkNavyStart else Color(0xFFCBD5E1),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = option.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(TealBright, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "âœ“",
                                            color = DarkNavyStart,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Fact-Checking Verification Focus
                Text(
                    text = "VERIFICATION FOCUS",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AiVerificationFocus.entries.forEach { focus ->
                        val isFocusSelected = selectedFocus == focus
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isFocusSelected) TealBright else Color(0x1F1E293B),
                            border = BorderStroke(
                                1.dp,
                                if (isFocusSelected) TealBright else Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFocus = focus }
                        ) {
                            Text(
                                text = focus.title,
                                color = if (isFocusSelected) DarkNavyStart else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // Security & Server Status Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x1A00E5FF),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "\uD83D\uDEE1\uFE0F", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Zero Client-Side Key Exposure",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "API keys are safely held on your PHP backend server (config/ai_config.php). Your Android client never exposes secret credentials.",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Server: " + NetworkConfig.currentBaseUrl + "ai_chat.php",
                            color = Color(0xFF64748B),
                            fontSize = 9.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            AiChatRepository.savePreferences(context, selectedModel, selectedFocus)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Config", color = DarkNavyStart, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDismiss() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
