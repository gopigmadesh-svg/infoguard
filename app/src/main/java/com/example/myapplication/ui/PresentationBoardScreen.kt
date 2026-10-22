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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PresentationBoardScreen(
    onBackClick: () -> Unit = {},
    onAskAiClick: () -> Unit = {}
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
                    center = Offset(size.width * 0.85f, size.height * 0.20f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.20f),
                radius = size.width * 0.75f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 1. Top Branding Header Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x331E293B),
                            border = BorderStroke(1.5.dp, TealGlow),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onBackClick() }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawShieldPath(color = TealBright, isStroke = true, strokeWidth = 3f)
                                    drawCheckmarkPath(color = Color.White, strokeWidth = 3f)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "TruthLens",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "MISINFORMATION CHECKER",
                                color = CyanAccent,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x2600E5FF),
                        border = BorderStroke(1.dp, Color(0x4D00E5FF))
                    ) {
                        Text(
                            text = "DESIGN SYSTEM BOARD",
                            color = CyanAccent,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Main Title Hero Banner with Glowing Shield
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
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
                                text = "ALL-IN-ONE ON-DEMAND TRUST",
                                color = TealBright,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "TruthLens — AI-Powered Misinformation Checker",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Think. Verify. Share.",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "A high-fidelity mobile experience designed to help people assess, inspect, and understand online information before sharing.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Large Glowing Shield Box
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Unspecified,
                            border = BorderStroke(1.5.dp, GlassBorder),
                            shadowElevation = 16.dp,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0x4000E5FF), Color(0x207C3AED))
                                        )
                                    )
                            ) {
                                Canvas(modifier = Modifier.size(36.dp)) {
                                    drawShieldPath(color = Color.White, isStroke = true, strokeWidth = 3.5f)
                                    drawCheckmarkPath(color = CyanAccent, strokeWidth = 4.5f)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Ask AI Assistant Section (Directly follows Hero section)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300E5FF))
                            ) {
                                Text(text = "✨", fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2600E5FF),
                                    border = BorderStroke(1.dp, Color(0x4D00E5FF))
                                ) {
                                    Text(
                                        text = "AI GUIDANCE",
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Ask AI Assistant ✨",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Text(
                            text = "Ask questions about news, misleading claims, and misinformation.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        // Sample AI Prompt Chips
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AiPromptSampleChip(text = "💬 \"Is this viral headline medically accurate?\"")
                            AiPromptSampleChip(text = "💬 \"How do I spot deepfake image manipulation?\"")
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Ask AI Button
                        Button(
                            onClick = onAskAiClick,
                            colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ask AI ✨",
                                    color = DarkNavyStart,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }

            // 4. Footer Branding
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TruthLens Mobile App",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Think. Verify. Share.",
                        color = TealBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AiPromptSampleChip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x1F1E293B),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PresentationBoardScreenPreview() {
    PresentationBoardScreen()
}
