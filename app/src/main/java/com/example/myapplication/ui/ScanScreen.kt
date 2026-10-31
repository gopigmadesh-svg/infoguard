package com.example.myapplication.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.myapplication.data.OcrProcessor
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ScanScreen(
    onBackClick: () -> Unit = {},
    onScanFinished: () -> Unit = {},
    onScanNewsClick: (String) -> Unit = {},
    onScanImageClick: (Uri?, String) -> Unit = { _, _ -> },
    onScanUrlClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isPreview = LocalInspectionMode.current

    var textClaim by remember { mutableStateOf("") }
    var urlClaim by remember { mutableStateOf("https://example.com/article") }

    // Image & Camera States
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessingOcr by remember { mutableStateOf(false) }
    var extractedOcrText by remember { mutableStateOf("") }

    fun processSelectedImage(uri: Uri) {
        val validation = OcrProcessor.validateImageUri(context, uri)
        if (!validation.isValid) {
            Toast.makeText(context, validation.errorMessage, Toast.LENGTH_LONG).show()
            return
        }

        selectedImageUri = uri
        isProcessingOcr = true

        coroutineScope.launch {
            val extractedText = OcrProcessor.extractTextFromImage(context, uri)
            extractedOcrText = extractedText
            if (extractedText.isNotBlank() && !extractedText.startsWith("No readable text")) {
                if (textClaim.isBlank()) {
                    textClaim = extractedText
                }
            }
            isProcessingOcr = false
        }
    }

    // Launchers
    val galleryLauncher = if (!isPreview) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { processSelectedImage(it) }
        }
    } else null

    val cameraLauncher = if (!isPreview) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success && tempCameraUri != null) {
                processSelectedImage(tempCameraUri!!)
            }
        }
    } else null

    val cameraPermissionLauncher = if (!isPreview) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val cameraUri = createTempImageUri(context)
                if (cameraUri != Uri.EMPTY) {
                    tempCameraUri = cameraUri
                    cameraLauncher?.launch(cameraUri)
                }
            } else {
                Toast.makeText(context, "Camera permission is required to take a photo.", Toast.LENGTH_SHORT).show()
            }
        }
    } else null

    fun launchCamera() {
        if (isPreview) return
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val cameraUri = createTempImageUri(context)
            if (cameraUri != Uri.EMPTY) {
                tempCameraUri = cameraUri
                cameraLauncher?.launch(cameraUri)
            }
        } else {
            cameraPermissionLauncher?.launch(Manifest.permission.CAMERA)
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

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { Spacer(modifier = Modifier.height(36.dp)) }

                // Top Header Bar
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
                                    text = "Check Information",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Choose an input method to verify claims.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // History Refresh Icon Button
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
                                    drawHistoryIcon(color = Color(0xFF94A3B8), strokeWidth = 3f)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // OPTION 1: NEWS TEXT INPUT CARD
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x2600E5FF))
                                ) {
                                    Text(text = "📄", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "OPTION 1",
                                        color = CyanAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Enter News Text",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = textClaim,
                                onValueChange = { if (it.length <= 1000) textClaim = it },
                                placeholder = {
                                    Text(
                                        text = "Paste a news headline, claim, or article text...",
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = Color(0x3300E5FF),
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            )

                            Text(
                                text = "${textClaim.length} / 1000",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.align(Alignment.End)
                            )

                            Button(
                                onClick = {
                                    if (textClaim.isNotBlank()) {
                                        onScanNewsClick(textClaim.trim())
                                    } else {
                                        Toast.makeText(context, "Please enter news text or a claim to verify.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔍 ", fontSize = 14.sp)
                                    Text(
                                        text = "Scan News",
                                        color = DarkNavyStart,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // OPTION 2: IMAGE UPLOAD / CAMERA CARD
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x337C3AED)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x267C3AED))
                                ) {
                                    Text(text = "🖼️", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "OPTION 2",
                                        color = PurpleInfo,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Upload Image or Take Photo",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (selectedImageUri != null) {
                                // Image Selected Preview
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Selected Image",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0x2600E5FF),
                                                border = BorderStroke(1.dp, Color(0x4D00E5FF)),
                                                modifier = Modifier.clickable { galleryLauncher?.launch("image/*") }
                                            ) {
                                                Text(
                                                    text = "Change",
                                                    color = CyanAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0x26F87171),
                                                border = BorderStroke(1.dp, Color(0x4DF87171)),
                                                modifier = Modifier.clickable {
                                                    selectedImageUri = null
                                                    extractedOcrText = ""
                                                }
                                            ) {
                                                Text(
                                                    text = "Remove 🗑️",
                                                    color = LowTrustRed,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF1E293B))
                                    ) {
                                        AsyncImage(
                                            model = selectedImageUri,
                                            contentDescription = "Uploaded / Captured Image Preview",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    if (isProcessingOcr) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                color = TealBright,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Extracting news text with OCR...",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        }
                                    } else if (extractedOcrText.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0x1F1E293B),
                                            border = BorderStroke(1.dp, Color(0x3300E5FF)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "EXTRACTED TEXT (OCR)",
                                                    color = CyanAccent,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    letterSpacing = 0.5.sp
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = extractedOcrText,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Choice Area: Take Photo or Gallery
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                                        drawRoundRect(
                                            color = Color(0x337C3AED),
                                            size = size,
                                            cornerRadius = CornerRadius(16.dp.toPx()),
                                            style = Stroke(width = 1.5.dp.toPx(), pathEffect = pathEffect)
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            text = "JPG, JPEG, or PNG up to 10MB supported",
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = { galleryLauncher?.launch("image/*") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x2600E5FF)),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, CyanAccent)
                                            ) {
                                                Text(
                                                    text = "🖼️ Upload from Gallery",
                                                    color = CyanAccent,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Button(
                                                onClick = { launchCamera() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x267C3AED)),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, PurpleInfo)
                                            ) {
                                                Text(
                                                    text = "📷 Take Photo",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { onScanImageClick(selectedImageUri, extractedOcrText) },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleInfo),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪄 ", fontSize = 14.sp)
                                    Text(
                                        text = "Scan Image",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // OPTION 3: WEBSITE OR ARTICLE URL CARD
                // ==========================================
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x33F59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x26F59E0B))
                                ) {
                                    Text(text = "🔗", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "OPTION 3",
                                        color = WarningYellow,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Enter Website or Article URL",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = urlClaim,
                                onValueChange = { urlClaim = it },
                                singleLine = true,
                                leadingIcon = {
                                    Box(modifier = Modifier.size(20.dp)) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawLinkIcon(color = WarningYellow, strokeWidth = 3f)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarningYellow,
                                    unfocusedBorderColor = Color(0x33F59E0B),
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = { onScanUrlClick(urlClaim) },
                                colors = ButtonDefaults.buttonColors(containerColor = WarningYellow),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⚡ ", fontSize = 14.sp)
                                    Text(
                                        text = "Scan URL",
                                        color = DarkNavyStart,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Security & Privacy Card
                item {
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
                                    text = "Secure & Private",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "All inputs are processed securely over encrypted connections.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Fixed Bottom Navigation Bar (Scan Tab Active)
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
                        isSelected = true,
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

// Helper to create temporary image Uri for Camera TakePicture contract
private fun createTempImageUri(context: Context): Uri {
    return try {
        val tempFile = File.createTempFile("camera_photo_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    } catch (_: Exception) {
        Uri.EMPTY
    }
}

@Preview(showBackground = true)
@Composable
fun ScanScreenPreview() {
    ScanScreen()
}
