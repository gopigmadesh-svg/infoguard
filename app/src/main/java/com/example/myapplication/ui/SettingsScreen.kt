package com.example.myapplication.ui

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onNavigateToPersonalInfo: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToPrivacySettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    val context = LocalContext.current

    var pushNotifications by remember { mutableStateOf(true) }
    var factCheckReminders by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(false) }

    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedTheme by remember { mutableStateOf("Dark Cyan") }
    var selectedTextScale by remember { mutableStateOf("Default (100%)") }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showTextScaleDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

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
                    SettingsRow(
                        iconEmoji = "👤",
                        title = "Personal Information",
                        onClick = onNavigateToPersonalInfo
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "🔑",
                        title = "Change Password",
                        onClick = onNavigateToChangePassword
                    )
                }
            }

            // 3. PREFERENCES Section
            item {
                SettingsSectionGroup(title = "PREFERENCES") {
                    SettingsRow(
                        iconEmoji = "🌐",
                        title = "Preferred Language",
                        trailingText = selectedLanguage,
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "📱",
                        title = "Content Categories",
                        onClick = onNavigateToPreferences
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "⚙️",
                        title = "Fact-Check Preferences",
                        onClick = onNavigateToPreferences
                    )
                }
            }

            // 4. FACT-CHECKING Section
            item {
                SettingsSectionGroup(title = "FACT-CHECKING") {
                    SettingsSwitchRow(
                        iconEmoji = "🔔",
                        title = "Push Notifications",
                        checked = pushNotifications,
                        onCheckedChange = {
                            pushNotifications = it
                            if (it) onNavigateToNotifications()
                        },
                        onClickRow = onNavigateToNotifications
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsSwitchRow(
                        iconEmoji = "⏱️",
                        title = "Fact-Check Reminders",
                        checked = factCheckReminders,
                        onCheckedChange = {
                            factCheckReminders = it
                            if (it) onNavigateToReminders()
                        },
                        onClickRow = onNavigateToReminders
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsSwitchRow(
                        iconEmoji = "📊",
                        title = "Weekly Summary",
                        checked = weeklySummary,
                        onCheckedChange = {
                            weeklySummary = it
                            Toast.makeText(
                                context,
                                if (it) "Weekly summary notifications enabled" else "Weekly summary disabled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

            // 5. APPEARANCE Section
            item {
                SettingsSectionGroup(title = "APPEARANCE") {
                    SettingsRow(
                        iconEmoji = "🌙",
                        title = "Dark Mode",
                        trailingText = selectedTheme,
                        onClick = { showThemeDialog = true }
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "🔤",
                        title = "Text Scale",
                        trailingText = selectedTextScale,
                        onClick = { showTextScaleDialog = true }
                    )
                }
            }

            // 6. PRIVACY Section
            item {
                SettingsSectionGroup(title = "PRIVACY") {
                    SettingsRow(
                        iconEmoji = "🗑️",
                        title = "Data Management",
                        onClick = onNavigateToPrivacySettings
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "⏱️",
                        title = "Clear Search History",
                        onClick = { showClearHistoryDialog = true }
                    )
                    HorizontalDivider(color = Color(0x1AFFFFFF))
                    SettingsRow(
                        iconEmoji = "🛡️",
                        title = "Privacy Settings",
                        onClick = onNavigateToPrivacySettings
                    )
                }
            }

            // 7. ABOUT Section
            item {
                SettingsSectionGroup(title = "ABOUT") {
                    SettingsRow(
                        iconEmoji = "ℹ️",
                        title = "About TruthLens",
                        onClick = onNavigateToAbout
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Dialog 1: Language Dialog
    if (showLanguageDialog) {
        SettingsSelectionDialog(
            title = "🌐 Select Preferred Language",
            options = listOf("English", "Spanish", "French", "German", "Chinese"),
            currentSelection = selectedLanguage,
            onOptionSelected = {
                selectedLanguage = it
                showLanguageDialog = false
                Toast.makeText(context, "Language set to $it", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Dialog 2: Theme / Dark Mode Dialog
    if (showThemeDialog) {
        SettingsSelectionDialog(
            title = "🌙 Select Appearance Theme",
            options = listOf("Dark Cyan", "Midnight Navy", "System Default"),
            currentSelection = selectedTheme,
            onOptionSelected = {
                selectedTheme = it
                showThemeDialog = false
                Toast.makeText(context, "Theme set to $it", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // Dialog 3: Text Scale Dialog
    if (showTextScaleDialog) {
        SettingsSelectionDialog(
            title = "🔤 Select Text Scale",
            options = listOf("Default (100%)", "Medium (115%)", "Large (130%)"),
            currentSelection = selectedTextScale,
            onOptionSelected = {
                selectedTextScale = it
                showTextScaleDialog = false
                Toast.makeText(context, "Text scale set to $it", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showTextScaleDialog = false }
        )
    }

    // Dialog 4: Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        Dialog(onDismissRequest = { showClearHistoryDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, CyanAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "⏱️ Clear Search History?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "This will clear your recent fact-check searches stored on this device. Bookmarked articles will remain saved.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showClearHistoryDialog = false
                                Toast.makeText(context, "Search history cleared successfully.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LowTrustRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear History", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x261E293B),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showClearHistoryDialog = false }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSelectionDialog(
    title: String,
    options: List<String>,
    currentSelection: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, CyanAccent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                options.forEach { option ->
                    val isSelected = option == currentSelection
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0x3300E5FF) else Color(0x1F1E293B),
                        border = BorderStroke(1.dp, if (isSelected) TealBright else Color(0x33FFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )

                            if (isSelected) {
                                Text(text = "✓", color = TealBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = DarkNavyStart, fontWeight = FontWeight.Bold)
                }
            }
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
    onCheckedChange: (Boolean) -> Unit,
    onClickRow: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickRow() }
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
