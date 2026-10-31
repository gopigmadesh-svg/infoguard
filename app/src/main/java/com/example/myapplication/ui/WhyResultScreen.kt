package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.myapplication.data.network.model.AnalysisResultDto

@Composable
fun WhyResultScreen(
    analysisResult: AnalysisResultDto? = null,
    onBackClick: () -> Unit = {}
) {
    val effectiveScore = analysisResult?.trustScore ?: 24
    val effectiveTitle = analysisResult?.title?.ifEmpty { null }
        ?: analysisResult?.content?.ifEmpty { null }
        ?: "\"Scientists Discover Miracle Cure for All Diseases\""
    val isMisleading = effectiveScore < 45 || analysisResult?.verdict?.contains("False", ignoreCase = true) == true
    val isUnverified = !isMisleading && (
        (analysisResult?.verdict?.contains("Unverified", ignoreCase = true) == true) ||
        (analysisResult?.verdict?.contains("Further", ignoreCase = true) == true) ||
        effectiveScore in 45..74
    )

    val verdictColor = when {
        isMisleading -> CoralWarningRed
        isUnverified -> WarningYellow
        else -> HighTrustGreen
    }

    val verdictText = analysisResult?.verdict?.uppercase() ?: if (isMisleading) "FALSE" else "VERIFIED / TRUE"

    val summaryText = analysisResult?.summary?.ifEmpty { null }
        ?: "The claim was evaluated against published records. Check the original publication and compare with independent, reliable sources."

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
                    center = Offset(size.width * 0.15f, size.height * 0.15f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.15f),
                radius = size.width * 0.6f
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
                            text = "Why This Result?",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "A transparent breakdown of the analysis.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 2. Summary Banner Box
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = verdictColor.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = verdictText,
                                    color = verdictColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "$effectiveScore% trust score",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "\"$effectiveTitle\"",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Gradient Risk Meter Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((effectiveScore / 100f).coerceIn(0.08f, 1f))
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = if (isMisleading) listOf(WarningYellow, CoralWarningRed) else listOf(CyanAccent, HighTrustGreen)
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            // 3. Breakdown Category Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val reasonsList = analysisResult?.reasons ?: emptyList()

                    BreakdownCategoryCard(
                        title = "Language Patterns",
                        subtitle = reasonsList.getOrNull(0) ?: "Sensational phrasing and absolute wording evaluated",
                        iconEmoji = "📄",
                        iconBgColor = Color(0x267C3AED)
                    )
                    BreakdownCategoryCard(
                        title = "Source Credibility",
                        subtitle = analysisResult?.sourceCredibility ?: "Evaluated against verified publisher indices",
                        iconEmoji = "⚠️",
                        iconBgColor = Color(0x26F59E0B)
                    )
                    BreakdownCategoryCard(
                        title = "Supporting Evidence",
                        subtitle = analysisResult?.supportingEvidence ?: "Independent cross-referencing completed",
                        iconEmoji = "🔍",
                        iconBgColor = if (isMisleading) Color(0x26F87171) else Color(0x2610B981)
                    )
                    BreakdownCategoryCard(
                        title = "Context & Nuance",
                        subtitle = analysisResult?.caveat ?: "Review original research for full methodology and sample size",
                        iconEmoji = "ℹ️",
                        iconBgColor = Color(0x2600E5FF)
                    )
                }
            }

            // 4. AI Recommendation Note Box
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0x266D28D9),
                    border = BorderStroke(1.dp, Color(0x4D7C3AED)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = summaryText,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 5. What To Verify Section
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
                            text = "WHAT TO VERIFY",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            VerifyCheckRow(text = "Find the original publication or press release")
                            VerifyCheckRow(text = "Check peer-reviewed studies or clinical trial databases")
                            VerifyCheckRow(text = "Verify reporting across independent wire services (Reuters, AP)")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun BreakdownCategoryCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    iconBgColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor)
                ) {
                    Text(text = iconEmoji, fontSize = 18.sp)
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
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "›",
                color = Color(0xFF64748B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VerifyCheckRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = HighTrustGreen.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, HighTrustGreen.copy(alpha = 0.4f)),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 2.5f)
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WhyResultScreenPreview() {
    WhyResultScreen()
}
