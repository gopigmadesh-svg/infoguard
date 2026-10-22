package com.example.myapplication.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// Theme Palette for InfoGuard
val DarkBackgroundStart = Color(0xFF030A16)
val DarkBackgroundMid = Color(0xFF08192D)
val DarkBackgroundEnd = Color(0xFF0D0F1D)
val CyanAccent = Color(0xFF00E5FF)
val CyanGlow = Color(0x3300E5FF)
val BlueAccent = Color(0xFF38BDF8)
val DarkGlassFill = Color(0x331E293B)
val GlassBorder = Color(0x4D00E5FF)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // Entrance Animations
    val scaleAnim = remember { Animatable(if (isPreview) 1f else 0.85f) }
    val alphaAnim = remember { Animatable(if (isPreview) 1f else 0f) }
    val progressAnim = remember { Animatable(if (isPreview) 0.7f else 0f) }

    // Pulse Animation for concentric rings
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    if (!isPreview) {
        LaunchedEffect(Unit) {
            // Entrance scale and alpha
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )

            // Loading bar progress over 2.2s
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
            )

            delay(300.milliseconds)
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundStart,
                        DarkBackgroundMid,
                        DarkBackgroundEnd
                    )
                )
            )
    ) {
        // Background Glows and Concentric Pulse Rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width * 0.5f, size.height * 0.42f)

            // Top-right light glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x3338BDF8), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = size.width * 0.65f
            )

            // Center cyan radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow, Color.Transparent),
                    center = centerOffset,
                    radius = size.width * 0.7f
                ),
                center = centerOffset,
                radius = size.width * 0.7f
            )

            // Concentric Glowing Rings around logo
            val baseRadius = size.width * 0.32f
            drawCircle(
                color = CyanAccent.copy(alpha = 0.12f * pulseAlpha),
                center = centerOffset,
                radius = baseRadius * pulseScale,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = CyanAccent.copy(alpha = 0.18f * pulseAlpha),
                center = centerOffset,
                radius = (baseRadius * 1.35f) * pulseScale,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = CyanAccent.copy(alpha = 0.08f * pulseAlpha),
                center = centerOffset,
                radius = (baseRadius * 1.7f) * pulseScale,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Main Layout Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header Branding (Top-Left style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alphaAnim.value),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkGlassFill,
                    border = BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawShieldPath(color = CyanAccent, isStroke = true, strokeWidth = 3f)
                            drawCheckmarkPath(color = Color.White, strokeWidth = 3.5f)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "TruthLens",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
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

            // 2. Central Hero Branding & Graphic
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                // Central Glassmorphism Icon Box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(144.dp)
                ) {
                    // Glass Card Container
                    Surface(
                        shape = RoundedCornerShape(36.dp),
                        color = Color(0x40111827),
                        border = BorderStroke(1.5.dp, GlassBorder),
                        shadowElevation = 20.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Radial inner highlight
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0x4000E5FF), Color.Transparent)
                                        )
                                    )
                            )

                            // Shield + Checkmark Canvas Drawing
                            Canvas(
                                modifier = Modifier
                                    .size(76.dp)
                                    .padding(4.dp)
                            ) {
                                // Draw shield stroke and fill
                                drawShieldPath(color = Color(0x3300E5FF), isStroke = false)
                                drawShieldPath(color = Color.White, isStroke = true, strokeWidth = 5f)
                                drawCheckmarkPath(color = CyanAccent, strokeWidth = 7f)
                            }
                        }
                    }

                    // Top-right Sparkles Canvas Drawing
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 14.dp, end = 14.dp)
                            .size(28.dp)
                    ) {
                        drawSparkleStar(
                            center = Offset(size.width * 0.7f, size.height * 0.3f),
                            sizePx = 10f,
                            color = CyanAccent
                        )
                        drawSparkleStar(
                            center = Offset(size.width * 0.3f, size.height * 0.7f),
                            sizePx = 6f,
                            color = BlueAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // App Title
                Text(
                    text = "TruthLens",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tagline / Subtitle
                Text(
                    text = "MISINFORMATION CHECKER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 2.2.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Pill Tag: "Think. Verify. Share."
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0x261E293B),
                    border = BorderStroke(1.dp, Color(0x4D38BDF8))
                ) {
                    Text(
                        text = "Think. Verify. Share.",
                        color = BlueAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 9.dp)
                    )
                }
            }

            // 3. Bottom Progress Bar & Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alphaAnim.value)
            ) {
                // Animated Progress Bar
                LinearProgressIndicator(
                    progress = { progressAnim.value },
                    modifier = Modifier
                        .width(150.dp)
                        .height(3.dp)
                        .clip(CircleShape),
                    color = CyanAccent,
                    trackColor = Color(0x331E293B)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Footer Text
                Text(
                    text = "ALL-IN-ONE DIGITAL TRUST",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
