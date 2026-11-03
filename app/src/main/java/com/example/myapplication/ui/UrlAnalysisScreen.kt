package com.example.myapplication.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.model.PageSignalDto

@Composable
fun UrlAnalysisScreen(
    initialUrl: String = "",
    analysisResult: AnalysisResultDto? = null,
    onBackClick: () -> Unit = {},
    onAnalyzePageClick: (String) -> Unit = {},
    onViewFullReportClick: () -> Unit = {},
    onCopyLinkClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var urlInput by remember(initialUrl, analysisResult) {
        mutableStateOf(
            analysisResult?.effectiveUrl
                ?: analysisResult?.content
                ?: initialUrl.ifBlank { "" }
        )
    }

    val isAnalyzed = analysisResult != null
    val displayDomain = remember(urlInput, analysisResult) {
        analysisResult?.domain
            ?: run {
                val cleaned = urlInput.trim().removePrefix("https://").removePrefix("http://").removePrefix("www.")
                cleaned.substringBefore("/").ifBlank { "example.com" }
            }
    }

    val initials = remember(displayDomain) {
        val parts = displayDomain.split(".")
        if (parts.isNotEmpty() && parts[0].length >= 2) {
            parts[0].take(2).uppercase()
        } else {
            "URL"
        }
    }

    val isHttps = remember(urlInput, analysisResult) {
        analysisResult?.isSslVerified ?: urlInput.trim().startsWith("https://", ignoreCase = true)
    }

    val scoreText = if (analysisResult?.isInsufficientEvidence == true || analysisResult?.trustScore == null) {
        "N/A"
    } else {
        "${analysisResult.trustScore}%"
    }

    val scoreColor = when {
        analysisResult?.isInsufficientEvidence == true || analysisResult?.trustScore == null -> WarningYellow
        (analysisResult?.trustScore ?: 0) >= 70 -> HighTrustGreen
        (analysisResult?.trustScore ?: 0) >= 40 -> WarningYellow
        else -> LowTrustRed
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
                                text = "URL Analysis",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Inspect a web page before trusting or sharing it.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Copy Link / Share Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                if (urlInput.isNotBlank()) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = ClipData.newPlainText("TruthLens URL", urlInput)
                                    clipboard?.setPrimaryClip(clip)
                                    Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                                onCopyLinkClick()
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = "🔗", fontSize = 16.sp)
                        }
                    }
                }
            }

            // 2. URL Input Card (Visible when entering or editing URL)
            if (!isAnalyzed) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WEBSITE OR ARTICLE LINK",
                                    color = CyanAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }

                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                placeholder = {
                                    Text(
                                        text = "https://example.com/article...",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = Color(0x3300E5FF),
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 3. Main Domain Profile Card
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
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0x332DD4BF),
                            border = BorderStroke(1.dp, Color(0x662DD4BF)),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = TealBright,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x2600E5FF),
                                border = BorderStroke(1.dp, Color(0x4D00E5FF))
                            ) {
                                Text(
                                    text = (analysisResult?.sourceCredibility ?: "WEB OUTLET").uppercase(),
                                    color = CyanAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (urlInput.isNotBlank()) urlInput else "Enter a website link above",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (isHttps) "HTTPS Connection • SSL Verified" else if (urlInput.isNotBlank()) "HTTP Connection • Unencrypted" else "Awaiting URL Input",
                                color = if (isHttps) HighTrustGreen else WarningYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (isAnalyzed) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = scoreColor.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = scoreText,
                                    color = scoreColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Analysis Assessment Card (When Analyzed)
            if (isAnalyzed) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ASSESSMENT VERDICT",
                                    color = scoreColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = scoreColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, scoreColor)
                                ) {
                                    Text(
                                        text = analysisResult.verdict.uppercase(),
                                        color = scoreColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = analysisResult.summary,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            if (!analysisResult.evidenceSupportingScore.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Evidence Basis: ${analysisResult.evidenceSupportingScore}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. PAGE SIGNALS Section
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
                            text = "PAGE SIGNALS",
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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (analysisResult?.pageSignals?.isNotEmpty() == true) {
                                analysisResult.pageSignals.forEach { signal ->
                                    val iconBg = when (signal.status) {
                                        "verified" -> Color(0x2610B981)
                                        "warning" -> Color(0x26F59E0B)
                                        "error" -> Color(0x26EF4444)
                                        else -> Color(0x2600E5FF)
                                    }
                                    PageSignalRow(
                                        iconEmoji = signal.iconEmoji.ifBlank { "🌐" },
                                        title = signal.title,
                                        subtitle = signal.subtitle,
                                        iconBgColor = iconBg
                                    )
                                }
                            } else {
                                val credSubtitle = if (analysisResult != null && analysisResult.trustScore != null && !analysisResult.isInsufficientEvidence) {
                                    "Score of ${analysisResult.trustScore}% calculated"
                                } else if (analysisResult != null) {
                                    "Score: N/A (Insufficient Evidence)"
                                } else {
                                    "Evaluates publisher authority and reputation"
                                }
                                PageSignalRow(
                                    iconEmoji = "🌐",
                                    title = "Domain Credibility",
                                    subtitle = credSubtitle,
                                    iconBgColor = Color(0x2600E5FF)
                                )

                                val authorSubtitle = when {
                                    !analysisResult?.reasons.isNullOrEmpty() -> analysisResult.reasons.firstOrNull { it.contains("author", ignoreCase = true) || it.contains("editorial", ignoreCase = true) } ?: "Editorial byline and source verification"
                                    else -> "Inspects author transparency and editorial disclosures"
                                }
                                PageSignalRow(
                                    iconEmoji = "👤",
                                    title = "Author Transparency",
                                    subtitle = authorSubtitle,
                                    iconBgColor = Color(0x267C3AED)
                                )

                                val secSubtitle = when {
                                    analysisResult != null && isHttps -> "Active SSL & Network Footprint Verified"
                                    analysisResult != null -> "Insecure Connection • Potential Risk"
                                    isHttps -> "HTTPS Connection • Active SSL Certificate"
                                    else -> "Inspects SSL certificate and server headers"
                                }
                                PageSignalRow(
                                    iconEmoji = "🛡️",
                                    title = "Security Footprint",
                                    subtitle = secSubtitle,
                                    iconBgColor = Color(0x2610B981)
                                )
                            }
                        }
                    }
                }
            }

            // 6. AI Context Disclaimer Note
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(modifier = Modifier.size(20.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLockIcon(color = TealBright, strokeWidth = 3f)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Content analysis evaluates domain history, author disclosures, and network trust metrics.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // 7. Action Button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                if (isAnalyzed) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onViewFullReportClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📊 ", fontSize = 16.sp)
                                Text(
                                    text = "View Full Report",
                                    color = DarkNavyStart,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                urlInput = ""
                                onAnalyzePageClick("")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x2600E5FF)),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, CyanAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Check Another URL",
                                color = CyanAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            val trimmed = urlInput.trim()
                            if (trimmed.isBlank()) {
                                Toast.makeText(context, "Please enter a website or article URL.", Toast.LENGTH_SHORT).show()
                            } else {
                                onAnalyzePageClick(trimmed)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡ ", fontSize = 16.sp)
                            Text(
                                text = "Analyze This Page",
                                color = DarkNavyStart,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun PageSignalRow(
    iconEmoji: String,
    title: String,
    subtitle: String,
    iconBgColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor)
            ) {
                Text(text = iconEmoji, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            text = "›",
            color = Color(0xFF64748B),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UrlAnalysisScreenPreview() {
    UrlAnalysisScreen()
}
