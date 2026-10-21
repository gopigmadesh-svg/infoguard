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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val HighTrustGreen = Color(0xFF10B981)
val LowTrustRed = Color(0xFFEF4444)
val WarningYellow = Color(0xFFF59E0B)

data class RecentScanItem(
    val title: String,
    val typeAndDate: String,
    val statusText: String,
    val statusColor: Color,
    val iconEmoji: String
)

@Composable
fun HomeScreen(
    onBackToSplash: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val recentScans = remember {
        listOf(
            RecentScanItem(
                title = "Lorem ipsum story",
                typeAndDate = "Article • Today",
                statusText = "VERIFIED",
                statusColor = HighTrustGreen,
                iconEmoji = "📄"
            ),
            RecentScanItem(
                title = "Global warming is a hoax",
                typeAndDate = "Claim • Yesterday",
                statusText = "FALSE",
                statusColor = LowTrustRed,
                iconEmoji = "🚨"
            ),
            RecentScanItem(
                title = "New vaccine news",
                typeAndDate = "Article • 2d ago",
                statusText = "UNCERTAIN",
                statusColor = WarningYellow,
                iconEmoji = "📄"
            )
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
        // Ambient background radial glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2000E5FF), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.20f),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.20f),
                radius = size.width * 0.65f
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(modifier = Modifier.height(36.dp)) }

                // 1. Top Header Greeting
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WELCOME BACK",
                                color = TealBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hello, Alex 👋",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Way to fact-check today, 84%.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }

                        // "Ask AI" Purple Badge Glass Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF6D28D9),
                            border = BorderStroke(1.dp, Color(0x66A78BFA)),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(width = 72.dp, height = 48.dp)
                                .clickable { onBackToSplash() }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Ask AI",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "✨",
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Trust Score Banner Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x401E293B)),
                        border = BorderStroke(1.dp, Color(0x4D00E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.70f)) {
                                Text(
                                    text = "YOUR TRUST SCORE",
                                    color = TealBright,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "92",
                                        color = Color.White,
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "/100",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HighTrustGreen.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, HighTrustGreen.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "RELIABLE",
                                        color = HighTrustGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Your recent checks show strong credibility patterns.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(72.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawAnalyticsShieldIcon(color = CyanAccent, strokeWidth = 4f)
                                }
                            }
                        }
                    }
                }

                // 3. Quick Actions Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "•• ",
                                color = TealBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "QUICK ACTIONS",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Text(
                            text = "View All",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Scan News",
                            subtitle = "Check text for misleading claims",
                            iconEmoji = "📄",
                            iconBgColor = Color(0x2600E5FF),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabSelected(1) }
                        )
                        QuickActionCard(
                            title = "Scan Image",
                            subtitle = "Inspect an image or visual claim",
                            iconEmoji = "🖼️",
                            iconBgColor = Color(0x267C3AED),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabSelected(1) }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Check URL",
                            subtitle = "Paste a website or article link",
                            iconEmoji = "🔗",
                            iconBgColor = Color(0x26F59E0B),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabSelected(1) }
                        )
                        QuickActionCard(
                            title = "Scan History",
                            subtitle = "View your previous checks",
                            iconEmoji = "⏱️",
                            iconBgColor = Color(0x2610B981),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabSelected(1) }
                        )
                    }
                }

                // 4. Recent Scans Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "•• ",
                                color = TealBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "RECENT SCANS",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Text(
                            text = "See History",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Recent Scans List
                items(recentScans) { item ->
                    RecentScanCard(item)
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
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
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        iconEmoji = "🏠",
                        label = "Home",
                        isSelected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            onTabSelected(0)
                        }
                    )
                    BottomNavItem(
                        iconEmoji = "🔍",
                        label = "Scan",
                        isSelected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            onTabSelected(1)
                        }
                    )
                    BottomNavItem(
                        iconEmoji = "⏱️",
                        label = "History",
                        isSelected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            onTabSelected(2)
                        }
                    )
                    BottomNavItem(
                        iconEmoji = "👤",
                        label = "Profile",
                        isSelected = selectedTab == 3,
                        onClick = {
                            selectedTab = 3
                            onTabSelected(3)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor)
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RecentScanCard(item: RecentScanItem) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
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
                        .background(item.statusColor.copy(alpha = 0.15f))
                ) {
                    Text(text = item.iconEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.typeAndDate,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = item.statusColor.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, item.statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = item.statusText,
                    color = item.statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    iconEmoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = iconEmoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) CyanAccent else Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 2.dp)
                    .clip(CircleShape)
                    .background(CyanAccent)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenDashboardPreview() {
    HomeScreen()
}
