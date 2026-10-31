package com.example.myapplication.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.network.model.AnalysisResultDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReportMetric(
    val label: String,
    val percentage: Int,
    val barColor: Color
)

@Composable
fun ReportScreen(
    analysisResult: AnalysisResultDto? = null,
    onBackClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    val currentDateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    val effectiveDate = analysisResult?.formattedDate ?: analysisResult?.timestamp ?: "Generated $currentDateStr"
    val effectiveScore = analysisResult?.trustScore ?: 62
    val effectiveVerdict = analysisResult?.verdict ?: "Needs Further Verification"
    val effectiveTitle = analysisResult?.title?.ifEmpty { null } ?: "Submitted News Analysis"
    val effectiveSummary = analysisResult?.summary?.ifEmpty { null }
        ?: "The claim contains language patterns that warrant caution. Due to ambiguous indicators, independent verification is recommended."
    val effectiveCredibility = analysisResult?.sourceCredibility ?: (if (effectiveScore >= 75) "Verified Source" else "Unverified / Caution")
    val effectiveDomain = analysisResult?.domain
        ?: analysisResult?.supportingSources?.firstOrNull()?.domain
        ?: "factcheck.org"

    val scoreColor = when {
        effectiveScore >= 75 -> HighTrustGreen
        effectiveScore >= 45 -> CyanAccent
        else -> CoralWarningRed
    }

    val defaultMetricColors = listOf(
        Color(0xFFA78BFA),
        Color(0xFFF59E0B),
        Color(0xFFF87171),
        Color(0xFF2DD4BF),
        Color(0xFFFBBF24)
    )

    val metrics = if (!analysisResult?.metrics.isNullOrEmpty()) {
        analysisResult!!.metrics.mapIndexed { index, m ->
            ReportMetric(
                label = m.label,
                percentage = m.percentage,
                barColor = defaultMetricColors[index % defaultMetricColors.size]
            )
        }
    } else {
        listOf(
            ReportMetric("Text Pattern Analysis", effectiveScore, Color(0xFFA78BFA)),
            ReportMetric("Source Credibility", maxOf(10, effectiveScore - 8), Color(0xFFF59E0B)),
            ReportMetric("Evidence Availability", maxOf(8, effectiveScore - 12), Color(0xFFF87171)),
            ReportMetric("Claim Consistency", minOf(98, effectiveScore + 4), Color(0xFF2DD4BF)),
            ReportMetric("Historical Accuracy", effectiveScore, Color(0xFFFBBF24))
        )
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
                                text = "Fact-Check Report",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = effectiveDate,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Top-right share button
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

            // 2. Overall Analysis Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, Color(0x4D00E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "OVERALL ANALYSIS",
                                color = TealBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = effectiveVerdict,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Evaluated across ${metrics.size} metrics",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Score Ring Box
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color(0x331E293B),
                                    style = Stroke(width = 6.dp.toPx())
                                )
                                val sweep = (effectiveScore / 100f) * 360f
                                drawArc(
                                    color = scoreColor,
                                    startAngle = -90f,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                text = "$effectiveScore%",
                                color = scoreColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // 3. Analysis Breakdown Metrics Chart Section
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
                            text = "ANALYSIS BREAKDOWN",
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
                            metrics.forEach { metric ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = metric.label,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${metric.percentage}%",
                                            color = metric.barColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    LinearProgressIndicator(
                                        progress = { metric.percentage / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(CircleShape),
                                        color = metric.barColor,
                                        trackColor = Color(0x331E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. AI Analysis Summary & Metadata Table
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
                            text = "AI ANALYSIS SUMMARY",
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
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = effectiveSummary,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = Color(0x1AFFFFFF))

                            Spacer(modifier = Modifier.height(14.dp))

                            // Metadata Key-Value Table Rows
                            ReportMetaRow(label = "Claim Content", value = if (effectiveTitle.length > 36) effectiveTitle.take(34) + "..." else effectiveTitle)
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportMetaRow(label = "Verdict", value = effectiveVerdict)
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportMetaRow(label = "Trust Score", value = "$effectiveScore%")
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportMetaRow(label = "Analysis Method", value = "TruthLens AI Engine")
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportMetaRow(label = "Source Verification", value = effectiveCredibility)
                            Spacer(modifier = Modifier.height(10.dp))
                            ReportMetaRow(label = "Primary Source", value = effectiveDomain)
                        }
                    }
                }
            }

            // 5. Action Buttons: Download & Share
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDownloadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📥 ", fontSize = 14.sp)
                            Text(
                                text = "Download",
                                color = DarkNavyStart,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clickable { onShareClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "📲 ", fontSize = 14.sp)
                            Text(
                                text = "Share",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 6. Footer Disclaimer Note
            item {
                Text(
                    text = "Fact-check report generated for personal information use only.",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ReportMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen()
}
