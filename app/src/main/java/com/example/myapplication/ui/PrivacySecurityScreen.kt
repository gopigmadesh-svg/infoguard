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
fun PrivacySecurityScreen(
    onBackClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    var biometricEnabled by remember { mutableStateOf(true) }
    var loginAlertsEnabled by remember { mutableStateOf(true) }

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
                                text = "Privacy & Security",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Control your data and account protection.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Security Shield Icon Button
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
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawShieldPath(color = TealBright, isStroke = true, strokeWidth = 3f)
                            }
                        }
                    }
                }
            }

            // 2. Security Status Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0x3310B981),
                                border = BorderStroke(1.dp, Color(0x6610B981)),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Canvas(modifier = Modifier.size(24.dp)) {
                                        drawShieldPath(color = HighTrustGreen, isStroke = true, strokeWidth = 3.5f)
                                        drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 3.5f)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x2600E5FF),
                                    border = BorderStroke(1.dp, Color(0x4D00E5FF))
                                ) {
                                    Text(
                                        text = "SECURITY STATUS",
                                        color = CyanAccent,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Your account is protected",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "2-Factor Auth Enabled • Strong Password",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HighTrustGreen.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, HighTrustGreen.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "SECURE",
                                color = HighTrustGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 3. PROTECTION Group
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
                            text = "PROTECTION",
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Biometric Lock
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔒 ", fontSize = 16.sp)
                                    Text(
                                        text = "Biometric Lock",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Switch(
                                    checked = biometricEnabled,
                                    onCheckedChange = { biometricEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DarkNavyStart,
                                        checkedTrackColor = TealBright,
                                        uncheckedThumbColor = Color(0xFF64748B),
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    )
                                )
                            }

                            HorizontalDivider(color = Color(0x1AFFFFFF))

                            // Login Alerts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📱 ", fontSize = 16.sp)
                                    Text(
                                        text = "Login Alerts",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Switch(
                                    checked = loginAlertsEnabled,
                                    onCheckedChange = { loginAlertsEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DarkNavyStart,
                                        checkedTrackColor = TealBright,
                                        uncheckedThumbColor = Color(0xFF64748B),
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    )
                                )
                            }

                            HorizontalDivider(color = Color(0x1AFFFFFF))

                            // Auto-lock session
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⏱️ ", fontSize = 16.sp)
                                    Text(
                                        text = "Auto-lock session",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "15 mins ›",
                                    color = CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4. YOUR DATA Group
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
                            text = "YOUR DATA",
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Download My Data
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📥 ", fontSize = 16.sp)
                                    Text(
                                        text = "Download My Data",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "›",
                                    color = Color(0xFF64748B),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color(0x1AFFFFFF))

                            // Clear Search History
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🗑️ ", fontSize = 16.sp)
                                    Text(
                                        text = "Clear Search History",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "›",
                                    color = Color(0xFF64748B),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color(0x1AFFFFFF))

                            // Delete Account
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDeleteAccountClick() },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🚨 ", fontSize = 16.sp)
                                    Text(
                                        text = "Delete Account",
                                        color = LowTrustRed,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "›",
                                    color = LowTrustRed,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 5. Privacy Disclaimer Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(modifier = Modifier.size(20.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLockIcon(color = TealBright, strokeWidth = 3f)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TruthLens does not sell personal data or use account for advertising.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacySecurityScreenPreview() {
    PrivacySecurityScreen()
}
