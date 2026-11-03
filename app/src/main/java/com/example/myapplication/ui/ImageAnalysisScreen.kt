package com.example.myapplication.ui

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.data.network.model.AnalysisResultDto

@Composable
fun ImageAnalysisScreen(
    analysisResult: AnalysisResultDto? = null,
    imageUri: Uri? = null,
    onBackClick: () -> Unit = {},
    onStartFullAnalysisClick: () -> Unit = {},
    onViewReportClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val isAnalyzed = analysisResult != null
    val scoreText = if (analysisResult?.isInsufficientEvidence == true || analysisResult?.trustScore == null) {
        "N/A"
    } else {
        "${analysisResult.trustScore}"
    }

    val scoreColor = when {
        analysisResult?.isInsufficientEvidence == true || analysisResult?.trustScore == null -> WarningYellow
        (analysisResult.trustScore ?: 0) >= 70 -> HighTrustGreen
        (analysisResult.trustScore ?: 0) >= 40 -> WarningYellow
        else -> LowTrustRed
    }

    val displayHeadline = when {
        !analysisResult?.title.isNullOrBlank() -> analysisResult.title
        !analysisResult?.content.isNullOrBlank() -> analysisResult.content
        imageUri != null -> "Selected Image for Verification"
        else -> "Visual Media Forensics"
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

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
                                text = "Image Analysis",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Visual claim and manipulation indicators.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Share Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onShareClick() }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = "📲", fontSize = 16.sp)
                        }
                    }
                }
            }

            // 2. Main Analyzed Image Preview Box
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Top-left IMAGE Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x2600E5FF),
                            border = BorderStroke(1.dp, Color(0x4D00E5FF)),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "IMAGE",
                                color = CyanAccent,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Center Image Display
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Analyzed Image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .align(Alignment.Center)
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(120.dp)
                                    .align(Alignment.Center)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(text = "🖼️", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = displayHeadline,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        // Bottom-right Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAnalyzed) Color(0x337C3AED) else Color(0x2600E5FF),
                            border = BorderStroke(1.dp, if (isAnalyzed) Color(0x66A78BFA) else Color(0x4D00E5FF)),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(
                                text = if (isAnalyzed) "ANALYZED" else "PENDING SCAN",
                                color = if (isAnalyzed) PurpleInfo else CyanAccent,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // 3. DETECTED SIGNALS Section
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
                            text = "DETECTED SIGNALS",
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
                            if (analysisResult?.imageSignals?.isNotEmpty() == true) {
                                analysisResult.imageSignals.forEach { signal ->
                                    val (emoji, bg) = when (signal.status.lowercase()) {
                                        "danger" -> "🚨" to Color(0x26F87171)
                                        "warning" -> "⚠️" to Color(0x26F59E0B)
                                        else -> "✓" to Color(0x2610B981)
                                    }
                                    SignalRow(
                                        iconEmoji = emoji,
                                        text = "${signal.label}: ${signal.description}",
                                        iconBgColor = bg
                                    )
                                }
                            } else if (analysisResult?.reasons?.isNotEmpty() == true) {
                                analysisResult.reasons.take(3).forEach { reason ->
                                    SignalRow(
                                        iconEmoji = "🔍",
                                        text = reason,
                                        iconBgColor = Color(0x2600E5FF)
                                    )
                                }
                            } else {
                                SignalRow(iconEmoji = "📄", text = "Text overlay & claim inspection", iconBgColor = Color(0x26F59E0B))
                                SignalRow(iconEmoji = "🔍", text = "Neural digital alteration analysis", iconBgColor = Color(0x2600E5FF))
                                SignalRow(iconEmoji = "✓", text = "Multimodal reverse corroboration", iconBgColor = Color(0x2610B981))
                            }
                        }
                    }
                }
            }

            // 4. CREDIBILITY SCORE Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Credibility Score",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = scoreText,
                                    color = scoreColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (scoreText != "N/A") {
                                    Text(
                                        text = " /100",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x261E293B),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                            modifier = Modifier.clickable {
                                if (isAnalyzed) onViewReportClick() else onStartFullAnalysisClick()
                            }
                        ) {
                            Text(
                                text = if (isAnalyzed) "View Report ›" else "Analyze Now ›",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 5. Action Button (Start Full Analysis or View Result)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (isAnalyzed) onViewReportClick() else onStartFullAnalysisClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (isAnalyzed) "📋 " else "🪄 ", fontSize = 16.sp)
                        Text(
                            text = if (isAnalyzed) "View Detailed Result" else "Start Full Analysis",
                            color = DarkNavyStart,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun SignalRow(iconEmoji: String, text: String, iconBgColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor)
        ) {
            Text(text = iconEmoji, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageAnalysisScreenPreview() {
    ImageAnalysisScreen()
}
