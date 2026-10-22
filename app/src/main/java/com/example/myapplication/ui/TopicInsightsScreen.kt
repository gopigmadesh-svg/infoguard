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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TrendingClaimItem(
    val id: String,
    val title: String,
    val categoryAndSearches: String,
    val statusText: String,
    val statusColor: Color,
    val iconEmoji: String,
    val iconBgColor: Color
)

@Composable
fun TopicInsightsScreen(
    onBackClick: () -> Unit = {},
    onClaimClick: (TrendingClaimItem) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val trendingClaims = listOf(
        TrendingClaimItem(
            id = "1",
            title = "Vitamin supplements and immunity",
            categoryAndSearches = "Health • 2.1k searches",
            statusText = "NEEDS CONTEXT",
            statusColor = WarningYellow,
            iconEmoji = "💊",
            iconBgColor = Color(0x26F59E0B)
        ),
        TrendingClaimItem(
            id = "2",
            title = "AI-generated election videos",
            categoryAndSearches = "Technology • 4.8k searches",
            statusText = "MISLEADING",
            statusColor = LowTrustRed,
            iconEmoji = "🤖",
            iconBgColor = Color(0x26F87171)
        ),
        TrendingClaimItem(
            id = "3",
            title = "New Solar Discovery",
            categoryAndSearches = "Science • 1.2k searches",
            statusText = "MOSTLY TRUE",
            statusColor = HighTrustGreen,
            iconEmoji = "🌱",
            iconBgColor = Color(0x2610B981)
        )
    )

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

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(36.dp)) }

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
                                    text = "Topic Insights",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Explore where your checked claims cluster.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Analytics Chart Icon Button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x261E293B),
                            border = BorderStroke(1.dp, Color(0x3300E5FF)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(text = "📊", fontSize = 16.sp)
                            }
                        }
                    }
                }

                // 2. Most Checked Topic Banner Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2600E5FF),
                                    border = BorderStroke(1.dp, Color(0x4D00E5FF))
                                ) {
                                    Text(
                                        text = "MOST CHECKED TOPIC",
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Health & Wellness",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "14 checks this month",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            // Circular Score Ring Box
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color(0x331E293B),
                                        style = Stroke(width = 5.dp.toPx())
                                    )
                                    drawArc(
                                        color = CyanAccent,
                                        startAngle = -90f,
                                        sweepAngle = 158f,
                                        useCenter = false,
                                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = "44%",
                                    color = CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // 3. YOUR ACTIVITY Progress Bars
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
                                text = "YOUR ACTIVITY",
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
                                TopicActivityRow(label = "Health", percentage = 76, barColor = TealBright)
                                TopicActivityRow(label = "Technology", percentage = 61, barColor = PurpleInfo)
                                TopicActivityRow(label = "Science", percentage = 48, barColor = HighTrustGreen)
                                TopicActivityRow(label = "Politics", percentage = 28, barColor = WarningYellow)
                            }
                        }
                    }
                }

                // 4. TRENDING CLAIMS Section
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TRENDING CLAIMS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                items(trendingClaims) { claim ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClaimClick(claim) }
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
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(claim.iconBgColor)
                                ) {
                                    Text(text = claim.iconEmoji, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = claim.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = claim.categoryAndSearches,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = claim.statusColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, claim.statusColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = claim.statusText,
                                        color = claim.statusColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "›",
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // 5. Fixed Bottom Navigation Bar
            Surface(
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0x3300E5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        iconEmoji = "🏠",
                        label = "Home",
                        isSelected = true,
                        onClick = { onTabSelected(0) }
                    )
                    BottomNavItem(
                        iconEmoji = "🔍",
                        label = "Scan",
                        isSelected = false,
                        onClick = { onTabSelected(1) }
                    )
                    BottomNavItem(
                        iconEmoji = "💡",
                        label = "Tips",
                        isSelected = false,
                        onClick = { onTabSelected(2) }
                    )
                    BottomNavItem(
                        iconEmoji = "⏱️",
                        label = "History",
                        isSelected = false,
                        onClick = { onTabSelected(3) }
                    )
                    BottomNavItem(
                        iconEmoji = "👤",
                        label = "Profile",
                        isSelected = false,
                        onClick = { onTabSelected(4) }
                    )
                }
            }
        }
    }
}

@Composable
fun TopicActivityRow(label: String, percentage: Int, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$percentage%",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = Color(0x331E293B)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopicInsightsScreenPreview() {
    TopicInsightsScreen()
}
