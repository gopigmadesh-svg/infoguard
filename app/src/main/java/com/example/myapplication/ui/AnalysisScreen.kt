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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun AnalysisScreen(
    onBackClick: () -> Unit = {},
    onAnalysisComplete: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // Progress counter animation from 0% to 68% to 100%
    val progressAnim = remember { Animatable(if (isPreview) 0.68f else 0f) }

    // Pulse animation for central glowing rings
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (!isPreview) {
        LaunchedEffect(Unit) {
            // Animate progress up to 68% first, then to 100%
            progressAnim.animateTo(
                targetValue = 0.68f,
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
            delay(1000.milliseconds)
            progressAnim.animateTo(
                targetValue = 1.00f,
                animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
            )
            delay(300.milliseconds)
            onAnalysisComplete()
        }
    }

    val currentProgress = (progressAnim.value * 100).toInt()

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
                    colors = listOf(Color(0x3300E5FF), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.30f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.30f),
                radius = size.width * 0.75f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Analyzing Information...",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "TruthLens AI is examining the submitted content.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 2. Central Hero Loading Animation & Percentage Display
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(220.dp)
                    ) {
                        // Concentric pulse rings
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerOffset = Offset(size.width * 0.5f, size.height * 0.5f)
                            val baseRadius = size.width * 0.36f
                            drawCircle(
                                color = CyanAccent.copy(alpha = 0.15f),
                                center = centerOffset,
                                radius = baseRadius * pulseScale,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                color = CyanAccent.copy(alpha = 0.08f),
                                center = centerOffset,
                                radius = (baseRadius * 1.35f) * pulseScale,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Central Hero Glass Icon Card
                        Surface(
                            shape = RoundedCornerShape(32.dp),
                            color = Color(0x401E293B),
                            border = BorderStroke(1.5.dp, GlassBorder),
                            shadowElevation = 16.dp,
                            modifier = Modifier.size(130.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(Color(0x4000E5FF), Color(0x207C3AED))
                                            )
                                        )
                                )

                                Canvas(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(4.dp)
                                ) {
                                    drawShieldPath(color = Color(0x3300E5FF), isStroke = false)
                                    drawShieldPath(color = Color.White, isStroke = true, strokeWidth = 4f)
                                    drawCheckmarkPath(color = CyanAccent, strokeWidth = 6f)
                                }
                            }
                        }

                        // Sparkle badge top right
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6D28D9),
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 28.dp, end = 28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "✨", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Percentage Display
                    Text(
                        text = "$currentProgress%",
                        color = Color(0xFF38BDF8),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "analysis in progress",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. Current Submission Progress Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0x3300E5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x2600E5FF))
                            ) {
                                Text(text = "📄", fontSize = 18.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Submitted news article claim...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Analyzing NLP semantics and sources",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Animated Progress Indicator
                        LinearProgressIndicator(
                            progress = { progressAnim.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape),
                            color = TealBright,
                            trackColor = Color(0x331E293B)
                        )
                    }
                }
            }

            // 4. Timeline Step-by-Step Checklist
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AnalysisStepRow(
                        title = "Text Pattern Analysis",
                        subtitle = "Completed",
                        isCompleted = true,
                        isInProgress = false
                    )
                    AnalysisStepRow(
                        title = "Source Credibility Check",
                        subtitle = "Completed",
                        isCompleted = true,
                        isInProgress = false
                    )
                    AnalysisStepRow(
                        title = "Claims and Evidence Review",
                        subtitle = if (currentProgress >= 68) "Reviewing evidence..." else "Waiting...",
                        isCompleted = currentProgress >= 90,
                        isInProgress = currentProgress in 60..89
                    )
                    AnalysisStepRow(
                        title = "Misinformation Risk Assessment",
                        subtitle = if (currentProgress >= 90) "Calculating score..." else "Waiting...",
                        isCompleted = currentProgress >= 100,
                        isInProgress = currentProgress >= 90
                    )
                }
            }

            // 5. Disclaimer Footer Note
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "AI analysis may not be 100% accurate. Verify important claims independently.",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AnalysisStepRow(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isInProgress: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isInProgress) Color(0x336D28D9) else Color(0xFF0F172A),
        border = BorderStroke(
            1.dp,
            if (isInProgress) Color(0x66A78BFA) else if (isCompleted) Color(0x3310B981) else Color(0x1AFFFFFF)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Step Status Icon Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) HighTrustGreen.copy(alpha = 0.2f)
                        else if (isInProgress) Color(0xFF6D28D9)
                        else Color(0xFF1E293B)
                    )
            ) {
                if (isCompleted) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 3f)
                    }
                } else if (isInProgress) {
                    Text(text = "⏳", fontSize = 11.sp)
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF64748B), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = if (isCompleted || isInProgress) Color.White else Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = if (isCompleted || isInProgress) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = if (isCompleted) HighTrustGreen else if (isInProgress) BlueAccent else Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    AnalysisScreen()
}
