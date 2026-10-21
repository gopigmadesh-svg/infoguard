package com.example.myapplication.ui

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette for Onboarding Screens
val TealBright = Color(0xFF2DD4BF)
val TealGlow = Color(0xFF14B8A6)
val PurpleCardStart = Color(0xFF5B21B6)
val PurpleCardEnd = Color(0xFF7C3AED)
val DarkNavyStart = Color(0xFF070D1A)
val DarkNavyEnd = Color(0xFF03070E)

@Composable
fun OnboardingScreen(
    initialPage: Int = 0,
    onGetStartedClick: () -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(initialPage) }

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
            val glowColor = if (currentPage == 0) Color(0x2000E5FF) else Color(0x207C3AED)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
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
            horizontalAlignment = Alignment.Start
        ) {
            // 1. Top Bar with App Logo Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

                // Page Switcher pill for quick preview/testing
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x261E293B),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.clickable {
                        currentPage = if (currentPage == 0) 1 else 0
                    }
                ) {
                    Text(
                        text = "Page ${currentPage + 1}/2",
                        color = CyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 2. Central Feature Graphic & Text Content Crossfade
            Crossfade(
                targetState = currentPage,
                label = "OnboardingPageTransition"
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (page == 0) {
                        OnboardingGraphicPage1()
                    } else {
                        OnboardingGraphicPage2()
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (page == 0) {
                        OnboardingTextContentPage1()
                    } else {
                        OnboardingTextContentPage2()
                    }
                }
            }

            // 3. Carousel Indicator & CTA Button
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Carousel Indicator Dots
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    // Page 1 Indicator
                    Box(
                        modifier = Modifier
                            .size(width = if (currentPage == 0) 28.dp else 6.dp, height = 6.dp)
                            .clip(CircleShape)
                            .background(if (currentPage == 0) TealBright else Color(0xFF334155))
                    )
                    // Page 2 Indicator
                    Box(
                        modifier = Modifier
                            .size(width = if (currentPage == 1) 28.dp else 6.dp, height = 6.dp)
                            .clip(CircleShape)
                            .background(if (currentPage == 1) TealBright else Color(0xFF334155))
                    )
                    // Page 3 Dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                    )
                }

                // Call To Action Button
                Button(
                    onClick = {
                        if (currentPage == 0) {
                            currentPage = 1
                        } else {
                            onGetStartedClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (currentPage == 0) "Get Started" else "Continue",
                        color = DarkNavyStart,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Graphics for Page 1: Detect Misinformation
@Composable
fun OnboardingGraphicPage1() {
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

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xEA0F172A),
                border = BorderStroke(1.dp, Color(0x3300E5FF)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    StatusDot(color = Color(0xFF38BDF8), label = "Secure")
                    StatusDot(color = Color(0xFFF59E0B), label = "Evidence")
                    StatusDot(color = Color(0xFF10B981), label = "Sources")
                }
            }
        }
    }
}

// Graphics for Page 2: Smarter Fact Checking
@Composable
fun OnboardingGraphicPage2() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x407C3AED), Color.Transparent)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.42f
            )
            drawCircle(
                color = Color(0x267C3AED),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.width * 0.38f,
                style = Stroke(width = 1.dp.toPx())
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

            // Purple AI Brain Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Unspecified,
                border = BorderStroke(1.dp, Color(0x66A78BFA)),
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
                                colors = listOf(PurpleCardStart, PurpleCardEnd)
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.size(54.dp)) {
                        drawBrainIcon(color = Color.White, strokeWidth = 3.5f)
                    }
                }
            }

            // Search Magnifying Badge on top-right of Purple Card
            Surface(
                shape = CircleShape,
                color = TealBright,
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.CenterEnd)
                    .padding(top = 8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawSearchIcon(color = DarkNavyStart, strokeWidth = 3f)
                    }
                }
            }

            // Bottom-Right Credibility Score Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xEA0F172A),
                border = BorderStroke(1.dp, Color(0x4D7C3AED)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "Credibility",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Multi-color rating meter bar
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(width = 10.dp, height = 3.dp).background(Color(0xFF10B981), CircleShape))
                            Box(modifier = Modifier.size(width = 10.dp, height = 3.dp).background(Color(0xFFF59E0B), CircleShape))
                            Box(modifier = Modifier.size(width = 10.dp, height = 3.dp).background(Color(0xFF38BDF8), CircleShape))
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "78",
                        color = Color(0xFF38BDF8),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// Text Content Page 1: Detect Misinformation
@Composable
fun OnboardingTextContentPage1() {
    Column {
        Text(
            text = "VERIFY INFORMATION",
            color = TealBright,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Detect Misinformation",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Check news, articles, headlines, and online content before you share.",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

// Text Content Page 2: Smarter Fact Checking
@Composable
fun OnboardingTextContentPage2() {
    Column {
        Text(
            text = "AI-POWERED ANALYSIS",
            color = TealBright,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Smarter Fact Checking",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "AI helps analyze text, identify misleading patterns, and highlight information that needs verification.",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPage1Preview() {
    OnboardingScreen(initialPage = 0)
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPage2Preview() {
    OnboardingScreen(initialPage = 1)
}
