package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

data class ReliableSource(
    val name: String,
    val dateOrType: String,
    val matchPercentage: Int
)

data class HistoryCheckItem(
    val id: String,
    val title: String,
    val categoryAndDate: String,
    val statusText: String,
    val statusColor: Color,
    val iconEmoji: String,
    val filterType: String,
    var isBookmarked: Boolean,
    val verdictReason: String,
    val explanation: String,
    val sources: List<ReliableSource>
)

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit = {},
    onItemClick: (HistoryCheckItem) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onStartCheckingClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var expandedCardId by remember { mutableStateOf<String?>(null) }

    val allChecks = remember {
        mutableStateListOf(
            HistoryCheckItem(
                id = "1",
                title = "Taking vitamin D daily boosts immunity",
                categoryAndDate = "Health • Today, 8:42 AM",
                statusText = "MOSTLY TRUE",
                statusColor = HighTrustGreen,
                iconEmoji = "📄",
                filterType = "Mostly True",
                isBookmarked = true,
                verdictReason = "Vitamin D is clinically verified to support immune system function.",
                explanation = "Medical reviews confirm adequate vitamin D levels support baseline immune health, though it does not guarantee prevention of viral infections.",
                sources = listOf(
                    ReliableSource("National Institutes of Health (NIH)", "Medical Review • 2023", 96),
                    ReliableSource("World Health Organization (WHO)", "Health Advisory", 92)
                )
            ),
            HistoryCheckItem(
                id = "2",
                title = "Global warming is a hoax",
                categoryAndDate = "Climate • Yesterday",
                statusText = "FALSE",
                statusColor = LowTrustRed,
                iconEmoji = "🚨",
                filterType = "False",
                isBookmarked = false,
                verdictReason = "Contradicts overwhelming global scientific consensus and satellite climate observations.",
                explanation = "Peer-reviewed research across major global meteorological agencies confirms long-term global warming trends driven by greenhouse gas emissions.",
                sources = listOf(
                    ReliableSource("NASA Climate Change Division", "Scientific Report • 2023", 98),
                    ReliableSource("IPCC Climate Assessment", "Global Study", 95)
                )
            ),
            HistoryCheckItem(
                id = "3",
                title = "AI-generated miracle cure claim",
                categoryAndDate = "Media • 2d ago",
                statusText = "UNVERIFIED",
                statusColor = WarningYellow,
                iconEmoji = "📄",
                filterType = "Misleading",
                isBookmarked = true,
                verdictReason = "No clinical trial data or peer-reviewed publication provided for the claim.",
                explanation = "The headline relies on sensationalized wording without primary clinical evidence or registered medical trial numbers.",
                sources = listOf(
                    ReliableSource("PubMed Clinical Trials Archive", "Database Query", 88)
                )
            ),
            HistoryCheckItem(
                id = "4",
                title = "New election law guidelines",
                categoryAndDate = "Politics • 3d ago",
                statusText = "MOSTLY TRUE",
                statusColor = HighTrustGreen,
                iconEmoji = "📄",
                filterType = "Mostly True",
                isBookmarked = false,
                verdictReason = "Official election commission updates align with published statutory guidelines.",
                explanation = "Statutory voting procedures match published administrative rules passed by state election boards.",
                sources = listOf(
                    ReliableSource("Federal Election Advisory Board", "Official Release • 2023", 94)
                )
            ),
            HistoryCheckItem(
                id = "5",
                title = "Vaccine side effects report",
                categoryAndDate = "Health • 5d ago",
                statusText = "NEEDS CONTEXT",
                statusColor = WarningYellow,
                iconEmoji = "📄",
                filterType = "Misleading",
                isBookmarked = false,
                verdictReason = "Presents rare adverse rates without statistical baseline context.",
                explanation = "Reported incidents are documented but occur in less than 0.001% of trial participants under controlled study monitoring.",
                sources = listOf(
                    ReliableSource("CDC Vaccine Safety Monitoring", "Surveillance Brief", 91)
                )
            )
        )
    }

    val filteredList = allChecks.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.categoryAndDate.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "Saved 🔖" -> item.isBookmarked
            else -> item.filterType == selectedFilter
        }
        matchesSearch && matchesFilter
    }

    val filterOptions = listOf("All", "Saved 🔖", "Mostly True", "False", "Misleading")

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

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Search History",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Review your previous fact-checks, evidence & bookmarks.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // 2. Search Bar with Filter Icon
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Search previous checks or sources...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Box(modifier = Modifier.size(20.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawSearchIcon(color = CyanAccent, strokeWidth = 3f)
                                }
                            }
                        },
                        trailingIcon = {
                            Text(text = "🌪️", fontSize = 16.sp)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = Color(0x3300E5FF),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. Category Filter Chips Row
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filterOptions) { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TealBright else Color(0xFF0F172A),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) TealBright else Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.clickable { selectedFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isSelected) DarkNavyStart else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Recent Section Header
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
                                text = if (selectedFilter == "Saved 🔖") "SAVED FACT CHECKS" else "RECENT CHECKS",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        val savedCount = allChecks.count { it.isBookmarked }
                        Text(
                            text = "$savedCount Saved 🔖",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 5. History List or Empty State
                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(text = "🔍", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Checks Found",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No items match your search or filter criteria.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredList) { item ->
                        val isExpanded = expandedCardId == item.id
                        EnhancedHistoryItemCard(
                            item = item,
                            isExpanded = isExpanded,
                            onCardClick = { onItemClick(item) },
                            onBookmarkToggle = {
                                val index = allChecks.indexOfFirst { c -> c.id == item.id }
                                if (index != -1) {
                                    val updated = allChecks[index].copy(isBookmarked = !allChecks[index].isBookmarked)
                                    allChecks[index] = updated
                                }
                            },
                            onToggleEvidence = {
                                expandedCardId = if (isExpanded) null else item.id
                            }
                        )
                    }
                }

                // 6. Monthly Stats Summary Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2600E5FF))
                            ) {
                                Canvas(modifier = Modifier.size(20.dp)) {
                                    drawHistoryIcon(color = CyanAccent, strokeWidth = 3f)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "12 checks this month",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "You've saved 23.5 hours checking fake news.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // 7. Fixed Bottom Navigation Bar
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
                        iconEmoji = "⏱️",
                        label = "History",
                        isSelected = true,
                        onClick = { onTabSelected(2) }
                    )
                    BottomNavItem(
                        iconEmoji = "👤",
                        label = "Profile",
                        isSelected = false,
                        onClick = { onTabSelected(3) }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedHistoryItemCard(
    item: HistoryCheckItem,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onToggleEvidence: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, if (isExpanded) CyanAccent else Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCardClick() }
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
                            text = item.categoryAndDate,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Status Badge + Bookmark Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = item.statusColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, item.statusColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = item.statusText,
                            color = item.statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Bookmark / Save Toggle Icon Button
                    Surface(
                        shape = CircleShape,
                        color = if (item.isBookmarked) Color(0x3300E5FF) else Color(0x1F1E293B),
                        border = BorderStroke(1.dp, if (item.isBookmarked) CyanAccent else Color(0x33FFFFFF)),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onBookmarkToggle() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (item.isBookmarked) "🔖" else "🏷️",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: "View Evidence & Sources"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleEvidence() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👁️ ", fontSize = 12.sp)
                    Text(
                        text = if (isExpanded) "Hide Evidence" else "View Evidence & Sources",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = CyanAccent,
                    fontSize = 10.sp
                )
            }

            // Expandable Evidence & Sources Drawer Panel
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Verdict Reason Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x1F1E293B),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Verdict Reason:",
                                color = item.statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.verdictReason,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.explanation,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reliable Sources List
                    Text(
                        text = "Reliable Sources:",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    item.sources.forEach { source ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0D1424),
                            border = BorderStroke(1.dp, Color(0x1A00E5FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "🌐 ", fontSize = 12.sp)
                                    Column {
                                        Text(
                                            text = source.name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = source.dateOrType,
                                            color = Color(0xFF64748B),
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HighTrustGreen.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, HighTrustGreen.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${source.matchPercentage}% Match",
                                        color = HighTrustGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenEnhancedPreview() {
    HistoryScreen()
}
