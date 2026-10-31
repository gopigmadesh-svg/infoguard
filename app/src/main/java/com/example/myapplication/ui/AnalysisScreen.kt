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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.repository.AnalysisRepository
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AnalysisScreen(
    claimText: String = "",
    userId: Int? = null,
    onBackClick: () -> Unit = {},
    onAnalysisComplete: () -> Unit = {},
    onAnalysisSuccess: (AnalysisResultDto) -> Unit = { _ -> },
    onError: (String) -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val analysisRepository = remember { AnalysisRepository() }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableStateOf(0) }
    var receivedResult by remember { mutableStateOf<AnalysisResultDto?>(null) }

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
        // Run API request and animation in tandem
        LaunchedEffect(retryTrigger) {
            errorMessage = null
            progressAnim.snapTo(0f)

            // Start smooth initial progress
            progressAnim.animateTo(
                targetValue = 0.65f,
                animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
            )

            val textToAnalyze = claimText.ifBlank { "Unspecified News Claim" }
            val repoResult = analysisRepository.analyzeClaim(userId, textToAnalyze)

            if (repoResult.isSuccess) {
                receivedResult = repoResult.getOrNull()
                // Complete animation to 100%
                progressAnim.animateTo(
                    targetValue = 1.00f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                )
                delay(200.milliseconds)
                receivedResult?.let {
                    onAnalysisSuccess(it)
                } ?: run {
                    onAnalysisComplete()
                }
            } else {
                val err = repoResult.exceptionOrNull()?.message ?: "Could not connect to TruthLens analysis server"
                errorMessage = err
                onError(err)
            }
        }
    }

    val currentProgress = (progressAnim.value * 100).toInt()
    val displayClaim = if (claimText.isNotBlank()) {
        if (claimText.length > 55) claimText.take(52) + "..." else claimText
    } else {
        "Submitted news article claim..."
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
                            text = "Deep claim verification & source cross-check.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Error Notice Banner if failure occurs
            if (errorMessage != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33F87171)),
                        border = BorderStroke(1.dp, CoralWarningRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "⚠️ Verification Error",
                                color = CoralWarningRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = errorMessage ?: "Unknown server error occurred",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { retryTrigger++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Retry", color = DarkNavyStart, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onBackClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x331E293B)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Go Back", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Central Radar Animation Scanner
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(240.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f

                            // Outer diffuse ripple ring
                            drawCircle(
                                color = Color(0x1A00E5FF),
                                radius = 110.dp.toPx() * pulseScale,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 1.dp.toPx())
                            )

                            // Middle glowing ring
                            drawCircle(
                                color = Color(0x3300E5FF),
                                radius = 85.dp.toPx() * pulseScale,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 1.5.dp.toPx())
                            )

                            // Innermost active glow disk
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x3300E5FF), Color.Transparent),
                                    center = Offset(centerX, centerY),
                                    radius = 65.dp.toPx()
                                ),
                                center = Offset(centerX, centerY),
                                radius = 65.dp.toPx()
                            )
                        }

                        // Central Badge Circle
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F172A),
                            border = BorderStroke(2.dp, TealBright),
                            modifier = Modifier.size(86.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.size(38.dp)) {
                                    drawShieldPath(color = Color(0x3300E5FF), isStroke = false)
                                    drawShieldPath(color = Color.White, isStroke = true, strokeWidth = 3f)
                                    drawCheckmarkPath(color = CyanAccent, strokeWidth = 4f)
                                }
                            }
                        }

                        // Orbiting Sparkle Star Pill
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopCenter)
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
                                    text = displayClaim,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
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
                        subtitle = if (currentProgress >= 30) "Completed" else "In Progress...",
                        isCompleted = currentProgress >= 30,
                        isInProgress = currentProgress < 30
                    )
                    AnalysisStepRow(
                        title = "Source Credibility Check",
                        subtitle = if (currentProgress >= 60) "Completed" else if (currentProgress >= 30) "In Progress..." else "Pending",
                        isCompleted = currentProgress >= 60,
                        isInProgress = currentProgress in 30..59
                    )
                    AnalysisStepRow(
                        title = "Claims and Evidence Review",
                        subtitle = if (currentProgress >= 90) "Completed" else if (currentProgress >= 60) "In Progress..." else "Pending",
                        isCompleted = currentProgress >= 90,
                        isInProgress = currentProgress in 60..89
                    )
                    AnalysisStepRow(
                        title = "Cross-Source Aggregation",
                        subtitle = if (currentProgress >= 100) "Completed" else if (currentProgress >= 90) "In Progress..." else "Pending",
                        isCompleted = currentProgress >= 100,
                        isInProgress = currentProgress in 90..99
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
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
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(
            width = 1.dp,
            color = if (isInProgress) TealBright.copy(alpha = 0.5f) else Color(0x1AFFFFFF)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = when {
                    isCompleted -> HighTrustGreen.copy(alpha = 0.2f)
                    isInProgress -> TealBright.copy(alpha = 0.2f)
                    else -> Color(0x1F1E293B)
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        isCompleted -> HighTrustGreen
                        isInProgress -> TealBright
                        else -> Color(0x33FFFFFF)
                    }
                ),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCompleted) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 2.5f)
                        }
                    } else if (isInProgress) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TealBright)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

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
                    color = if (isInProgress) TealBright else Color(0xFF64748B),
                    fontSize = 11.sp,
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
