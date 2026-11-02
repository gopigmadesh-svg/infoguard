package com.example.myapplication.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.network.repository.ProfileRepository
import com.example.myapplication.data.network.repository.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    onSavePreferences: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val profileRepo = remember { ProfileRepository() }

    val currentUserId = remember { if (sessionManager.currentUserId > 0) sessionManager.currentUserId else 1 }
    var name by remember { mutableStateOf(sessionManager.currentUserName.ifEmpty { "TruthLens User" }) }
    var bio by remember { mutableStateOf(sessionManager.currentUserBio) }
    var language by remember { mutableStateOf("English (US)") }
    var alertEnabled by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    val selectedInterests = remember {
        mutableStateListOf("News", "Science", "Technology")
    }

    val selectedContentTypes = remember {
        mutableStateListOf("Text", "Articles")
    }

    val allInterests = listOf("News", "Politics", "Science", "Health", "Technology", "Social Media")

    LaunchedEffect(currentUserId) {
        if (!isPreview && currentUserId > 0) {
            val result = profileRepo.getProfile(currentUserId)
            result.onSuccess { profile ->
                if (profile.name.isNotBlank()) name = profile.name
                if (!profile.bio.isNullOrBlank()) bio = profile.bio
                if (profile.language.isNotBlank()) language = profile.language
                alertEnabled = profile.notificationsEnabled
                if (profile.interests.isNotEmpty()) {
                    selectedInterests.clear()
                    selectedInterests.addAll(profile.interests)
                }
            }
        }
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
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Header with Back Button and Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
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
                            text = "Personalize Your Experience",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Update your profile information and preferences.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. Your Name Field
            item {
                Column {
                    Text(
                        text = "YOUR NAME",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        leadingIcon = {
                            Box(modifier = Modifier.size(20.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawPersonIcon(color = CyanAccent, strokeWidth = 3f)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. Your Bio Field
            item {
                Column {
                    Text(
                        text = "BIO / ABOUT",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        placeholder = {
                            Text(
                                text = "Add a short bio or description...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 4. Preferred Language Field
            item {
                Column {
                    Text(
                        text = "PREFERRED LANGUAGE",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = language,
                        onValueChange = { language = it },
                        singleLine = true,
                        leadingIcon = {
                            Box(modifier = Modifier.size(20.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawGlobeIcon(color = CyanAccent, strokeWidth = 3f)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 5. Main Interests (Choice Chips)
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "MAIN INTERESTS",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allInterests.forEach { interest ->
                            val isSelected = selectedInterests.contains(interest)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.White else Color(0x33FFFFFF)
                                ),
                                modifier = Modifier.clickable {
                                    if (isSelected) selectedInterests.remove(interest)
                                    else selectedInterests.add(interest)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    if (isSelected) {
                                        Text(
                                            text = "✓ ",
                                            color = DarkNavyStart,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = interest,
                                        color = if (isSelected) DarkNavyStart else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Preferred Content (3-Card Grid Row)
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "•• ",
                            color = TealBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PREFERRED CONTENT",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ContentCard(
                            title = "Text",
                            iconEmoji = "📄",
                            isSelected = selectedContentTypes.contains("Text"),
                            onSelect = {
                                if (selectedContentTypes.contains("Text")) selectedContentTypes.remove("Text")
                                else selectedContentTypes.add("Text")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ContentCard(
                            title = "Articles",
                            iconEmoji = "🌐",
                            isSelected = selectedContentTypes.contains("Articles"),
                            onSelect = {
                                if (selectedContentTypes.contains("Articles")) selectedContentTypes.remove("Articles")
                                else selectedContentTypes.add("Articles")
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ContentCard(
                            title = "Images",
                            iconEmoji = "🖼️",
                            isSelected = selectedContentTypes.contains("Images"),
                            onSelect = {
                                if (selectedContentTypes.contains("Images")) selectedContentTypes.remove("Images")
                                else selectedContentTypes.add("Images")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 7. Fact-Checking Preferences
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
                            text = "FACT-CHECKING PREFERENCES",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Misinformation Alert Toggle Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Misinformation Alert",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Receive real-time alerts for viral claims",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = alertEnabled,
                                onCheckedChange = { alertEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DarkNavyStart,
                                    checkedTrackColor = TealBright,
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                )
                            )
                        }
                    }

                    // Privacy First Info Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(modifier = Modifier.size(24.dp)) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLockIcon(color = TealBright, strokeWidth = 3f)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Privacy First",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Your preferences are stored locally and can be updated anytime.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // 8. Save Preferences Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    enabled = !isSaving,
                    onClick = {
                        if (!isPreview) {
                            isSaving = true
                            coroutineScope.launch {
                                val result = profileRepo.updateProfile(
                                    userId = currentUserId,
                                    name = name.trim(),
                                    bio = bio.trim(),
                                    language = language,
                                    interests = selectedInterests.toList(),
                                    notificationsEnabled = alertEnabled
                                )
                                isSaving = false
                                result.onSuccess { updatedProfile ->
                                    sessionManager.saveUserProfile(updatedProfile)
                                    Toast.makeText(context, "Preferences saved successfully!", Toast.LENGTH_SHORT).show()
                                    onSavePreferences()
                                }.onFailure { err ->
                                    sessionManager.updateProfile(name = name.trim(), bio = bio.trim())
                                    Toast.makeText(context, "Preferences saved locally.", Toast.LENGTH_SHORT).show()
                                    onSavePreferences()
                                }
                            }
                        } else {
                            onSavePreferences()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = DarkNavyStart,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "Save Preferences",
                            color = DarkNavyStart,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContentCard(
    title: String,
    iconEmoji: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFE0F2FE) else Color(0xFF0F172A),
        border = BorderStroke(1.dp, if (isSelected) CyanAccent else Color(0x33FFFFFF)),
        modifier = modifier
            .height(72.dp)
            .clickable { onSelect() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = iconEmoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isSelected) DarkNavyStart else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreen()
}
