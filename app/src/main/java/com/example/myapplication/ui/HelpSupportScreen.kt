package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FaqItemData(
    val question: String,
    val answer: String
)

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit = {},
    onStartConversationClick: () -> Unit = {},
    onReportIssueClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val faqList = listOf(
        FaqItemData(
            question = "How does AI fact-checking work?",
            answer = "TruthLens evaluates headlines, articles, images, and links against verified databases, primary research repositories, and publisher metadata to rate claim credibility."
        ),
        FaqItemData(
            question = "What does 'Needs Context' mean?",
            answer = "This rating indicates the claim contains truthful elements but leaves out key statistical, historical, or scientific context needed to avoid misinterpretation."
        ),
        FaqItemData(
            question = "How is my personal data protected?",
            answer = "All scanned text, links, and photos are processed anonymously over encrypted HTTPS connections. Search history is stored strictly on your local device."
        ),
        FaqItemData(
            question = "Can I export a fact-check report?",
            answer = "Yes, tapping 'Download PDF' on any Result or Analytics Report screen lets you save an official TruthLens verification sheet to your device."
        )
    )

    val filteredFaqs = faqList.filter {
        it.question.contains(searchQuery, ignoreCase = true) ||
                it.answer.contains(searchQuery, ignoreCase = true)
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
                    center = Offset(size.width * 0.5f, size.height * 0.20f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.20f),
                radius = size.width * 0.75f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 1. Top Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            text = "Help & Support",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Find answers or contact the TruthLens team.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 2. Top Welcome Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300E5FF))
                        ) {
                            Text(text = "✨", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "How can we help?",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Search guidance for checking queries, privacy, and your account.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 3. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Search help articles...",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Box(modifier = Modifier.size(20.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawSearchIcon(color = CyanAccent, strokeWidth = 3f)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Color(0x3300E5FF),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. POPULAR QUESTIONS Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "POPULAR QUESTIONS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            filteredFaqs.forEachIndexed { index, faq ->
                                val isExpanded = expandedFaqIndex == index

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedFaqIndex = if (isExpanded) null else index
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = faq.question,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = if (isExpanded) "▲" else "›",
                                            color = CyanAccent,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = faq.answer,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    if (index < filteredFaqs.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = Color(0x1AFFFFFF))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. CONTACT SUPPORT Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "CONTACT SUPPORT",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Card 1: Message us
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onStartConversationClick() }
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "💬", fontSize = 20.sp)
                                Text(
                                    text = "Message us",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Typical response: < 15 mins",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Card 2: Report an issue
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onReportIssueClick() }
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🏷️", fontSize = 20.sp)
                                Text(
                                    text = "Report an issue",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Submit bugs or feedback",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 6. Action Button: Start a Conversation
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onStartConversationClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Start a Conversation",
                        color = DarkNavyStart,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpSupportScreenPreview() {
    HelpSupportScreen()
}
