package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyStateScreen(
    onStartCheckingClick: () -> Unit = {}
) {
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
                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.width * 0.7f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar with App Badge
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

            // 2. Central Hero Graphic & Text Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Central Dashed Container Box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                        drawRoundRect(
                            color = Color(0x3300E5FF),
                            size = size,
                            cornerRadius = CornerRadius(28.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx(), pathEffect = pathEffect)
                        )

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x3314B8A6), Color.Transparent)
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.5f),
                            radius = size.width * 0.38f
                        )
                    }

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

                        // Overlapping Teal Search Card
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
                                Canvas(modifier = Modifier.size(48.dp)) {
                                    drawSearchIcon(color = Color.White, strokeWidth = 4f)
                                }
                            }
                        }

                        // Small Verified Badge attached on top right of Teal Card
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
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Title: No Checks Yet
                Text(
                    text = "No Checks Yet",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "Start by scanning a headline, pasting text, uploading an image, or entering a URL to explore item credibility.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // 3. CTA Button & Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onStartCheckingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Start Checking",
                        color = DarkNavyStart,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your checks will appear here",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateScreenPreview() {
    EmptyStateScreen()
}
