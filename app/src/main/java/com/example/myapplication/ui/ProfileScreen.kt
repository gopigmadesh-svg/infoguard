package com.example.myapplication.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.network.ProfilePictureUploader
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.myapplication.data.network.repository.ProfileRepository
import com.example.myapplication.data.network.repository.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.myapplication.data.OcrProcessor
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onNavigateToPersonalInfo: () -> Unit = {},
    onNavigateToSavedArticles: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToPrivacySecurity: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelpSupport: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val profileRepo = remember { ProfileRepository() }

    val currentUserId = remember(sessionManager.currentUserId) { if (sessionManager.currentUserId > 0) sessionManager.currentUserId else 1 }
    var userName by remember(sessionManager.currentUserName) { mutableStateOf(sessionManager.currentUserName.ifEmpty { "Alex Morgan" }) }
    var userEmail by remember(sessionManager.currentUserEmail) { mutableStateOf(sessionManager.currentUserEmail.ifEmpty { "alex.morgan@example.com" }) }
    var userBio by remember { mutableStateOf("Senior Digital Media Researcher") }
    var accountType by remember { mutableStateOf("Standard Account • Active") }
    var checksCount by remember { mutableStateOf(12) }
    var savedCount by remember { mutableStateOf(5) }

    val allInterests = listOf("SCIENCE", "TECHNOLOGY", "HEALTH", "CURRENT AFFAIRS")
    var selectedInterests by remember { mutableStateOf(setOf("SCIENCE", "TECHNOLOGY", "HEALTH")) }
    var selectedLanguage by remember { mutableStateOf("English (US)") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Persistent Profile Image Uri State
    var profileImageUri by remember {
        mutableStateOf<Uri?>(
            ProfilePictureUploader.getSavedProfilePicture(context).let { savedUrl ->
                if (!savedUrl.isNullOrBlank()) Uri.parse(savedUrl) else File(context.filesDir, "truthlens_profile_avatar.jpg").let { file ->
                if (file.exists()) Uri.fromFile(file) else null }
            }
        )
    }

    LaunchedEffect(currentUserId) {
        if (!isPreview) {
            val result = profileRepo.getProfile(currentUserId)
            result.onSuccess { profile ->
                if (profile.name.isNotBlank()) userName = profile.name
                if (profile.email.isNotBlank()) userEmail = profile.email
                if (!profile.bio.isNullOrBlank()) userBio = profile.bio
                accountType = profile.accountType
                checksCount = profile.checksCount
                savedCount = profile.savedCount
                if (profile.interests.isNotEmpty()) {
                    selectedInterests = profile.interests.toSet()
                }
                if (profile.language.isNotBlank()) {
                    selectedLanguage = profile.language
                }
                val remoteAvatar = profile.avatarUrl ?: profile.profilePicture
                if (!remoteAvatar.isNullOrBlank()) {
                    profileImageUri = Uri.parse(remoteAvatar)
                    ProfilePictureUploader.saveSavedProfilePicture(context, remoteAvatar)
                }
            }
        }
    }

    // Gallery Photo Picker Launcher
    val galleryLauncher = if (!isPreview) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { sourceUri: Uri? ->
            if (sourceUri != null) {
                val validation = OcrProcessor.validateImageUri(context, sourceUri)
                if (validation.isValid) {
                    try {
                        val destFile = File(context.filesDir, "truthlens_profile_avatar.jpg")
                        context.contentResolver.openInputStream(sourceUri)?.use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        profileImageUri = Uri.fromFile(destFile)
                        Toast.makeText(context, "Profile picture saved locally. Uploading to server...", Toast.LENGTH_SHORT).show()
                        ProfilePictureUploader.uploadProfilePicture(
                            context,
                            sourceUri,
                            currentUserId,
                            userEmail,
                            object : ProfilePictureUploader.UploadCallback {
                                override fun onProgress(progressPercent: Int) {}
                                override fun onSuccess(serverUrl: String) {
                                    profileImageUri = Uri.parse(serverUrl)
                                    Toast.makeText(context, "Uploaded to server successfully!", Toast.LENGTH_SHORT).show()
                                }
                                override fun onError(errorMessage: String) {
                                    Toast.makeText(context, "Server upload error: $errorMessage", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    } catch (_: Exception) {
                        Toast.makeText(context, "Could not save profile picture.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, validation.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    } else null

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
                    colors = listOf(Color(0x207C3AED), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.15f),
                    radius = size.width * 0.65f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.15f),
                radius = size.width * 0.65f
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
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x331E293B),
                            border = BorderStroke(1.5.dp, TealGlow),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawShieldPath(color = TealBright, isStroke = true, strokeWidth = 3f)
                                    drawCheckmarkPath(color = Color.White, strokeWidth = 3f)
                                }
                            }
                        }

                        // Top-right settings button
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x261E293B),
                            border = BorderStroke(1.dp, Color(0x3300E5FF)),
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { onNavigateToSettings() }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(text = "⚙️", fontSize = 16.sp)
                            }
                        }
                    }
                }

                // 2. User Info Hero Card Section
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            // Circular Profile Picture Avatar
                            Surface(
                                shape = CircleShape,
                                color = Color.Unspecified,
                                border = BorderStroke(2.dp, CyanAccent),
                                shadowElevation = 12.dp,
                                modifier = Modifier
                                    .size(86.dp)
                                    .clickable { galleryLauncher?.launch("image/*") }
                            ) {
                                if (profileImageUri != null) {
                                    AsyncImage(
                                        model = profileImageUri,
                                        contentDescription = "Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(PurpleCardStart, PurpleCardEnd)
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = if (userName.trim().isNotEmpty()) userName.trim().split(" ").filter { it.isNotEmpty() }.let { if (it.size >= 2) "${it[0].take(1)}${it[1].take(1)}".uppercase() else it[0].take(2).uppercase() } else "AM",
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }

                            // Small Camera / Edit Badge Icon
                            Surface(
                                shape = CircleShape,
                                color = TealBright,
                                border = BorderStroke(1.5.dp, Color.White),
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable { galleryLauncher?.launch("image/*") }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "📷", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.clickable { onNavigateToPersonalInfo() }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = userEmail,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onNavigateToPersonalInfo() }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Account Type Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2600E5FF),
                            border = BorderStroke(1.dp, Color(0x4D00E5FF)),
                            modifier = Modifier.clickable { onNavigateToPersonalInfo() }
                        ) {
                            Text(
                                text = "🏷️ $accountType",
                                color = CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // 3. User Metrics Stats Row (3 Interactive Cards)
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileStatCard(
                            number = checksCount.toString(),
                            label = "Checks",
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToHistory() }
                        )
                        ProfileStatCard(
                            number = savedCount.toString(),
                            label = "Saved",
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToSavedArticles() }
                        )
                        ProfileStatCard(
                            number = "${selectedInterests.size}",
                            label = "Topics",
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }
                        )
                    }
                }

                // 4. Interactive Interests Section
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
                                text = "INTERESTS (TAP TO SELECT)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allInterests.forEach { interest ->
                                val isSelected = selectedInterests.contains(interest)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0x3300E5FF) else Color(0xFF0F172A),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) TealBright else Color(0x33FFFFFF)
                                    ),
                                    modifier = Modifier.clickable {
                                        val updated = selectedInterests.toMutableSet()
                                        if (isSelected) {
                                            updated.remove(interest)
                                        } else {
                                            updated.add(interest)
                                        }
                                        selectedInterests = updated
                                        if (!isPreview) {
                                            coroutineScope.launch {
                                                profileRepo.updateProfile(
                                                    userId = currentUserId,
                                                    interests = updated.toList()
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        if (isSelected) {
                                            Text(text = "✓ ", color = TealBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = interest,
                                            color = if (isSelected) CyanAccent else Color(0xFF64748B),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Account Options Menu Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "•• ",
                                color = TealBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ACCOUNT",
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
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                AccountOptionRow(iconEmoji = "👤", title = "Personal Information", onClick = onNavigateToPersonalInfo)
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(iconEmoji = "🔖", title = "Saved Articles", onClick = onNavigateToSavedArticles)
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(iconEmoji = "⏱️", title = "Search History", onClick = onNavigateToHistory)
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(
                                    iconEmoji = "🌐",
                                    title = "Language Preferences ($selectedLanguage)",
                                    onClick = { showLanguageDialog = true }
                                )
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(iconEmoji = "🔒", title = "Privacy & Security", onClick = onNavigateToPrivacySecurity)
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(iconEmoji = "⚙️", title = "Settings", onClick = onNavigateToSettings)
                                HorizontalDivider(color = Color(0x1AFFFFFF))
                                AccountOptionRow(iconEmoji = "🎧", title = "Help & Support", onClick = onNavigateToHelpSupport)
                            }
                        }
                    }
                }

                // 6. Log Out Button
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x1AF87171),
                        border = BorderStroke(1.dp, Color(0x4DF87171)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { onLogOutClick() }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "🚪 ", fontSize = 14.sp)
                            Text(
                                text = "Log Out",
                                color = CoralWarningRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }

            // 7. Fixed Bottom Navigation Bar (Profile Tab Active)
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
                        isSelected = true,
                        onClick = { onTabSelected(4) }
                    )
                }
            }
        }
    }

    // Language Selection Modal Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onLanguageSelected = { lang ->
                selectedLanguage = lang
                showLanguageDialog = false
                if (!isPreview) {
                    coroutineScope.launch {
                        profileRepo.updateProfile(
                            userId = currentUserId,
                            language = lang
                        )
                    }
                }
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf("English (US)", "Spanish (Español)", "French (Français)", "German (Deutsch)", "Chinese (中文)")

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
                    text = "🌐 Select Preferred Language",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Choose your preferred language for article analysis and verification summaries.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                languages.forEach { lang ->
                    val isSelected = lang == currentLanguage
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0x3300E5FF) else Color(0x1F1E293B),
                        border = BorderStroke(1.dp, if (isSelected) TealBright else Color(0x33FFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(lang) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang,
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
fun ProfileStatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AccountOptionRow(iconEmoji: String, title: String, onClick: () -> Unit) {
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
        Text(
            text = "›",
            color = Color(0xFF64748B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
