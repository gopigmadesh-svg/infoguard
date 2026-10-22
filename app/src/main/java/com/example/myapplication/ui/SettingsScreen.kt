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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {}
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var factCheckReminders by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(false) }

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
                    center = Offset(size.width * 0.15f, size.height * 0.15f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.15f),
                radius = size.width * 0.6f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Manage your info-check experience.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 2. ACCOUNT Section
            item {
                SettingsSectionGroup(title = "ACCOUNT") {
                    SettingsRow(iconEmoji = "👤", title = "Personal Information")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "🔑", title = "Change Password")
                }
            }

            // 3. PREFERENCES Section
            item {
                SettingsSectionGroup(title = "PREFERENCES") {
                    SettingsRow(iconEmoji = "🌐", title = "Preferred Language", trailingText = "English")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "📱", title = "Content Categories")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "⚙️", title = "Fact-Check Preferences")
                }
            }

            // 4. FACT-CHECKING Section
            item {
                SettingsSectionGroup(title = "FACT-CHECKING") {
                    SettingsSwitchRow(
                        iconEmoji = "🔔",
                        title = "Push Notifications",
                        checked = pushNotifications,
                        onCheckedChange = { pushNotifications = it }
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsSwitchRow(
                        iconEmoji = "⏱️",
                        title = "Fact-Check Reminders",
                        checked = factCheckReminders,
                        onCheckedChange = {
                            factCheckReminders = it
                            if (it) onNavigateToReminders()
                        }
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsSwitchRow(
                        iconEmoji = "📊",
                        title = "Weekly Summary",
                        checked = weeklySummary,
                        onCheckedChange = { weeklySummary = it }
                    )
                }
            }

            // 5. APPEARANCE Section
            item {
                SettingsSectionGroup(title = "APPEARANCE") {
                    SettingsRow(iconEmoji = "🌙", title = "Dark Mode", trailingText = "Dark Cyan")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "🔤", title = "Text Scale")
                }
            }

            // 6. PRIVACY Section
            item {
                SettingsSectionGroup(title = "PRIVACY") {
                    SettingsRow(iconEmoji = "🗑️", title = "Data Management")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "⏱️", title = "Clear Search History")
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(iconEmoji = "🛡️", title = "Privacy Settings")
                }
            }

            // 7. ABOUT Section
            item {
                SettingsSectionGroup(title = "ABOUT") {
                    SettingsRow(iconEmoji = "ℹ️", title = "About TruthLens")
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun SettingsSectionGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "•• ",
                color = TealBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
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
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    iconEmoji: String,
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = "›",
                color = Color(0xFF64748B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    iconEmoji: String,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DarkNavyStart,
                    checkedTrackColor = TealBright,
                    uncheckedThumbColor = Color(0xFF94A3B8),
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "›",
                color = Color(0xFF64748B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
