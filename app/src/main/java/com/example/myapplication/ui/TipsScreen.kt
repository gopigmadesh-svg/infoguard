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

data class MisinformationTip(
    val id: String,
    val iconEmoji: String,
    val title: String,
    val description: String,
    val categoryTag: String,
    val tagColor: Color
)

@Composable
fun TipsScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val tipsList = listOf(
        MisinformationTip(
            id = "1",
            iconEmoji = "🔍",
            title = "Check the Source & Domain",
            description = "Look closely at the URL. Impostor sites often spoof legitimate news domain names with slight spelling variations or odd extensions.",
            categoryTag = "SOURCE VERIFICATION",
            tagColor = CyanAccent
        ),
        MisinformationTip(
            id = "2",
            iconEmoji = "📰",
            title = "Read Beyond the Headline",
            description = "Headlines often exaggerate facts to drive clicks. Read the entire article to confirm the body content actually supports the headline claim.",
            categoryTag = "CONTENT ANALYSIS",
            tagColor = TealBright
        ),
        MisinformationTip(
            id = "3",
            iconEmoji = "👤",
            title = "Verify Author & Publisher",
            description = "Check if the author is a recognized journalist. Reputable news outlets maintain transparent editorial guidelines and contact disclosures.",
            categoryTag = "AUTHOR CREDIBILITY",
            tagColor = PurpleInfo
        ),
        MisinformationTip(
            id = "4",
            iconEmoji = "📅",
            title = "Check the Date & Timestamp",
            description = "Old news reports and outdated statistics are frequently re-shared as current events to create confusion during breaking news.",
            categoryTag = "TIMELINESS",
            tagColor = WarningYellow
        ),
        MisinformationTip(
            id = "5",
            iconEmoji = "🔬",
            title = "Look for Supporting Evidence",
            description = "Authentic reports cite official medical studies, expert quotes, or official documents. Be cautious if no primary sources are provided.",
            categoryTag = "EVIDENCE",
            tagColor = HighTrustGreen
        ),
        MisinformationTip(
            id = "6",
            iconEmoji = "🖼️",
            title = "Reverse Image Search Visuals",
            description = "Photos can be taken out of context or altered. Performing a reverse image lookup helps verify when and where an image was originally taken.",
            categoryTag = "MEDIA INTEGRITY",
            tagColor = CyanAccent
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
                    center = Offset(size.width * 0.5f, size.height * 0.15f),
                    radius = size.width * 0.75f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.15f),
                radius = size.width * 0.75f
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
                                    text = "Fact-Check Tips",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Guidelines to identify misleading news and claims.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Top Lightbulb Icon
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
                                Text(text = "💡", fontSize = 18.sp)
                            }
                        }
                    }
                }

                // 2. Featured Golden Rule Banner
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300E5FF))
                            ) {
                                Text(text = "💡", fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2600E5FF),
                                    border = BorderStroke(1.dp, Color(0x4D00E5FF))
                                ) {
                                    Text(
                                        text = "RULE OF THUMB",
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Pause Before You Share",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "Sensational headlines provoke emotional reactions. Verify before re-posting.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // 3. Tips List Section Header
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PRACTICAL GUIDELINES",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                // 4. Practical Tips Items
                items(tipsList) { tip ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(tip.tagColor.copy(alpha = 0.15f))
                                    ) {
                                        Text(text = tip.iconEmoji, fontSize = 18.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = tip.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = tip.tagColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, tip.tagColor.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = tip.categoryTag,
                                        color = tip.tagColor,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = tip.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // 5. Fixed 5-Item Bottom Navigation Bar
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
                        isSelected = false,
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
                        isSelected = true,
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

@Preview(showBackground = true)
@Composable
fun TipsScreenPreview() {
    TipsScreen()
}
