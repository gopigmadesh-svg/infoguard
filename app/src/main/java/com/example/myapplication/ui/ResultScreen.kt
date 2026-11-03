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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

val CoralWarningRed = Color(0xFFF87171)

enum class ResultType {
    MISLEADING,
    MOSTLY_TRUE
}

@Composable
fun ResultScreen(
    analysisResult: AnalysisResultDto? = null,
    initialResultType: ResultType = ResultType.MOSTLY_TRUE,
    onBackClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    var resultTypeState by remember { mutableIntStateOf(if (initialResultType == ResultType.MISLEADING) 0 else 1) }

    val effectiveScore = analysisResult?.trustScore ?: if (resultTypeState == 0) 24 else 74
    val rawVerdict = analysisResult?.verdict ?: ""
    val isInsufficient = analysisResult?.isInsufficientEvidence == true ||
        analysisResult?.trustScore == null ||
        rawVerdict.contains("Further", ignoreCase = true) ||
        rawVerdict.contains("Unverified", ignoreCase = true) ||
        rawVerdict.contains("Insufficient", ignoreCase = true)

    val isAiGenerated = !isInsufficient && (rawVerdict.contains("AI-Generated", ignoreCase = true) || rawVerdict.contains("Synthetic", ignoreCase = true))

    val isFalse = if (analysisResult != null) {
        !isInsufficient && !isAiGenerated && (rawVerdict.contains("False", ignoreCase = true) || rawVerdict.contains("Fake", ignoreCase = true) || rawVerdict.contains("Manipulated", ignoreCase = true) || effectiveScore <= 35)
    } else {
        resultTypeState == 0
    }

    val isMisleading = !isFalse && !isInsufficient && !isAiGenerated && (
        rawVerdict.contains("Misleading", ignoreCase = true) ||
        rawVerdict.contains("Partial", ignoreCase = true) ||
        rawVerdict.contains("Exaggerat", ignoreCase = true)
    )

    val isUnverified = isInsufficient || (!isFalse && !isMisleading && !isAiGenerated && (
        rawVerdict.contains("Unverified", ignoreCase = true) ||
        rawVerdict.contains("Further", ignoreCase = true) ||
        effectiveScore in 36..69
    ))

    val themeColor = when {
        isAiGenerated -> Color(0xFFA855F7)
        isFalse -> CoralWarningRed
        isMisleading -> Color(0xFFFB923C) // Amber Orange
        isUnverified -> WarningYellow
        else -> HighTrustGreen
    }

    val headlineText = analysisResult?.verdict?.ifEmpty { null }
        ?: when {
            isAiGenerated -> "AI-Generated"
            isFalse -> "False"
            isMisleading -> "Misleading"
            isUnverified -> "Needs Further Verification"
            else -> "Verified / True"
        }

    val statusTag = when {
        isInsufficient -> "INSUFFICIENT EVIDENCE / UNVERIFIED"
        isAiGenerated -> "AI-GENERATED SYNTHETIC MEDIA"
        rawVerdict.contains("Manipulated", ignoreCase = true) -> "MANIPULATED / MISLEADING"
        isFalse -> "FALSE / HIGH RISK"
        isMisleading -> "MISLEADING / PARTIAL TRUTH"
        isUnverified -> "NEEDS FURTHER VERIFICATION"
        else -> "VERIFIED / AUTHENTIC"
    }

    val riskTopTag = analysisResult?.riskLevel ?: when {
        isFalse -> "🚨 Critical Risk"
        isMisleading -> "⚠️ High Risk"
        isUnverified -> "ℹ️ Caution"
        else -> "✓ Minimal Risk"
    }

    val analyzedText = analysisResult?.title?.ifEmpty { null }
        ?: analysisResult?.content?.ifEmpty { null }
        ?: if (isMisleading) "\"Scientists Discover Miracle Cure for All Diseases\"" else "\"Taking vitamin D daily boosts immunity\""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkNavyStart, DarkNavyEnd)
                )
            )
    ) {
        // Ambient background glow matching verdict theme
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(themeColor.copy(alpha = 0.22f), Color.Transparent),
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

                    Text(
                        text = "Fact-Check Result",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )

                    // Verdict Indicator Tag
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f)),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = riskTopTag,
                                color = themeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Main Verdict Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Verdict Icon Box
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(themeColor.copy(alpha = 0.2f))
                                .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        ) {
                            if (isMisleading) {
                                Text(text = "⚠️", fontSize = 28.sp)
                            } else if (isUnverified) {
                                Text(text = "🔍", fontSize = 26.sp)
                            } else {
                                Canvas(modifier = Modifier.size(28.dp)) {
                                    drawShieldPath(color = themeColor, isStroke = true, strokeWidth = 3.5f)
                                    drawCheckmarkPath(color = Color.White, strokeWidth = 4f)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Tag Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = themeColor.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = statusTag,
                                color = themeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Verdict Headline
                        Text(
                            text = headlineText,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Match Prediction Confidence Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isInsufficient) "N/A" else "$effectiveScore%",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isInsufficient) "Insufficient Evidence" else "Trust Score Assessment",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            // Circular Progress Arc Ring
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color(0x331E293B),
                                        style = Stroke(width = 5.dp.toPx())
                                    )
                                    val sweep = if (isInsufficient) 180f else (effectiveScore / 100f) * 360f
                                    drawArc(
                                        color = themeColor,
                                        startAngle = -90f,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = if (isInsufficient) "N/A" else "$effectiveScore%",
                                    color = themeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Quoted Analyzed Content Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    text = "Text analyzed:",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"$analyzedText\"",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Risk Classification Card
            if (!analysisResult?.riskLevel.isNullOrBlank() || !analysisResult?.riskExplanation.isNullOrBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, themeColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🛡️ RISK CLASSIFICATION: ",
                                    color = themeColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = analysisResult?.riskLevel ?: "Evaluated",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = analysisResult?.riskExplanation ?: "Evaluated against public news wires, registries, and fact-checking archives.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // Sub-Claims Decomposition Card (For Compound Claims)
            if (!analysisResult?.subClaims.isNullOrEmpty()) {
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
                                text = "COMPOUND CLAIM DECOMPOSITION",
                                color = Color.White,
                                fontSize = 12.sp,
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
                                analysisResult!!.subClaims.forEach { sc ->
                                    val scColor = when (sc.status.uppercase()) {
                                        "VERIFIED" -> HighTrustGreen
                                        "FALSE" -> CoralWarningRed
                                        "MISLEADING" -> Color(0xFFFB923C)
                                        else -> WarningYellow
                                    }
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = scColor.copy(alpha = 0.18f),
                                                border = BorderStroke(1.dp, scColor.copy(alpha = 0.5f))
                                            ) {
                                                Text(
                                                    text = sc.status.uppercase(),
                                                    color = scColor,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = sc.statement,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 16.sp
                                        )
                                        if (!sc.details.isNullOrBlank()) {
                                            Text(
                                                text = sc.details,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Middle Section: Claim Assessment or Flagged Reasons
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
                            text = if (isMisleading) "WHY IT WAS FLAGGED" else "CLAIM ASSESSMENT",
                            color = Color.White,
                            fontSize = 12.sp,
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
                            if (!analysisResult?.evidenceSupportingScore.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = themeColor.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "EVIDENCE BASIS FOR VERDICT & SCORE",
                                            color = themeColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.8.sp
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = analysisResult!!.evidenceSupportingScore!!,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            if (!analysisResult?.reasons.isNullOrEmpty()) {
                                analysisResult!!.reasons.forEach { reason ->
                                    if (isMisleading) {
                                        FlagItemRow(iconEmoji = "🚨", text = reason)
                                    } else {
                                        AssessmentRow(
                                            tagLabel = if (isUnverified) "CONTEXT" else "ACCURATE",
                                            tagColor = if (isUnverified) WarningYellow else HighTrustGreen,
                                            description = reason
                                        )
                                    }
                                }
                            } else {
                                if (isMisleading) {
                                    FlagItemRow(iconEmoji = "🚨", text = "The claim uses exaggerated or unsupported assertions.")
                                    FlagItemRow(iconEmoji = "📄", text = "No reliable supporting source was identified.")
                                    FlagItemRow(iconEmoji = "⚠️", text = "Contradicts verified scientific or historical records.")
                                } else {
                                    AssessmentRow(tagLabel = "ACCURATE", tagColor = HighTrustGreen, description = "Statement aligns with verified published consensus.")
                                    AssessmentRow(tagLabel = "CONTEXT", tagColor = BlueAccent, description = "Review primary source records for exact trial and observation details.")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Supporting Sources Section
            if (!analysisResult?.supportingSources.isNullOrEmpty()) {
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
                                text = "SUPPORTING SOURCES",
                                color = Color.White,
                                fontSize = 12.sp,
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
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                analysisResult!!.supportingSources.forEach { source ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = themeColor.copy(alpha = 0.15f),
                                                border = BorderStroke(1.dp, themeColor.copy(alpha = 0.4f)),
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = source.name.take(3).uppercase(),
                                                        color = themeColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column {
                                                Text(
                                                    text = source.name,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = source.domain ?: "Verified Source",
                                                    color = Color(0xFF64748B),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = themeColor.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = "${source.matchPercentage}% Match",
                                                color = themeColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    if (!source.details.isNullOrBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0x1F1E293B),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = source.details,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Analysis Summary Section
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
                            text = "ANALYSIS SUMMARY",
                            color = Color.White,
                            fontSize = 12.sp,
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
                        Text(
                            text = analysisResult?.summary?.ifEmpty { null }
                                ?: if (isMisleading)
                                    "Automated analysis identified patterns commonly associated with misleading claims. You should check the referenced trusted media sources."
                                else
                                    "The claim aligns with factual reporting standards and lacks typical misinformation patterns.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 6. Action Buttons Row: View Details & Share Result
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onViewDetailsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "View Details",
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
                            .clickable { onShareClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "📲 ", fontSize = 14.sp)
                            Text(
                                text = "Share Result",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 7. Footer Disclaimer Note
            item {
                Text(
                    text = "A trusted source for your daily news report.",
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
fun AssessmentRow(tagLabel: String, tagColor: Color, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = tagColor.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, tagColor.copy(alpha = 0.4f))
        ) {
            Text(
                text = tagLabel,
                color = tagColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = description,
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FlagItemRow(iconEmoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x26F87171))
        ) {
            Text(text = iconEmoji, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenMisleadingPreview() {
    ResultScreen(initialResultType = ResultType.MISLEADING)
}

@Preview(showBackground = true)
@Composable
fun ResultScreenMostlyTruePreview() {
    ResultScreen(initialResultType = ResultType.MOSTLY_TRUE)
}
