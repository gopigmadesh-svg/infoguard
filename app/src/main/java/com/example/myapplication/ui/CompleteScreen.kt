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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.network.model.AnalysisResultDto

@Composable
fun CompleteScreen(
    analysisResult: AnalysisResultDto? = null,
    onViewResultClick: () -> Unit = {},
    onBackToHomeClick: () -> Unit = {}
) {
    val displayTitle = analysisResult?.title?.ifEmpty { null } ?: "Submitted news article claim..."
    val displayTag = analysisResult?.verdict?.uppercase() ?: "FACT-CHECK COMPLETE"
    val displayDate = analysisResult?.formattedDate ?: "Completed just now"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkNavyStart, DarkNavyEnd)
                )
            )
    ) {
        // Ambient background glow (cyan accent)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2000E5FF), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.width * 0.7f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar with App Badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x331E293B),
                            border = BorderStroke(1.5.dp, TealGlow),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawShieldPath(color = TealBright, isStroke = true, strokeWidth = 3f)
                                    drawCheckmarkPath(color = Color.White, strokeWidth = 3f)
                                }
                            }
                        }

                        // Top-right notification dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFFA855F7), CircleShape)
                        )
                    }
                }
            }

            // 2. Central Hero Graphic & Text Section
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(240.dp)
                    ) {
                        // Document Sheet Card
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFFF8FAFC),
                            shadowElevation = 12.dp,
                            modifier = Modifier
                                .size(width = 135.dp, height = 165.dp)
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                drawDocumentSheet(
                                    fillColor = Color(0xFFF8FAFC),
                                    lineColor = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Overlapping Teal Shield Card
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color.Unspecified,
                            border = BorderStroke(1.dp, Color(0x665EEAD4)),
                            shadowElevation = 16.dp,
                            modifier = Modifier
                                .size(width = 110.dp, height = 110.dp)
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                                        )
                                    )
                            ) {
                                Canvas(modifier = Modifier.size(52.dp)) {
                                    drawShieldPath(color = Color.White, isStroke = true, strokeWidth = 4f)
                                    drawCheckmarkPath(color = TealBright, strokeWidth = 5f)
                                }
                            }
                        }

                        // Small Verified Check Badge on top right of Teal Card
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0D9488),
                            border = BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.CenterEnd)
                                .padding(top = 8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(12.dp)) {
                                    drawCheckmarkPath(color = Color.White, strokeWidth = 3f)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title: Fact-Check Complete
                    Text(
                        text = "Fact-Check Complete",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle
                    Text(
                        text = "Your content has been analyzed. Review the verdict and supporting evidence before sharing.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submission Summary Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x2610B981))
                            ) {
                                Canvas(modifier = Modifier.size(20.dp)) {
                                    drawShieldPath(color = HighTrustGreen, isStroke = true, strokeWidth = 3f)
                                    drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 3f)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2600E5FF),
                                    border = BorderStroke(1.dp, Color(0x4D00E5FF))
                                ) {
                                    Text(
                                        text = displayTag,
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = displayTitle,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )

                                Text(
                                    text = displayDate,
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. CTA Action Buttons & Footer
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onViewResultClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text(
                                text = "View Result",
                                color = DarkNavyStart,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0x261E293B),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clickable { onBackToHomeClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "Back to Home",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Think. Verify. Share.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompleteScreenPreview() {
    CompleteScreen()
}
