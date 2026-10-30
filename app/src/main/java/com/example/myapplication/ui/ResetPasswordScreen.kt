package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import android.widget.Toast
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.network.repository.AuthRepository
import com.example.myapplication.data.network.repository.SessionManager
import com.example.myapplication.ui.viewmodel.AuthViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel? = null,
    email: String = "",
    onUpdatePasswordSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = authViewModel ?: remember {
        val sessionManager = SessionManager(context)
        AuthViewModel(AuthRepository(sessionManager))
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val serverError by viewModel.errorMessage.collectAsState()

    val targetEmail = email.ifEmpty { viewModel.resetEmail.value }

    var newPassword by remember { mutableStateOf("NewSecurePass123!") }
    var confirmPassword by remember { mutableStateOf("NewSecurePass123!") }
    var localError by remember { mutableStateOf<String?>(null) }

    val hasMinLength = newPassword.length >= 8
    val hasNumberAndSymbol = newPassword.any { it.isDigit() } && newPassword.any { !it.isLetterOrDigit() }
    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword

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
                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.width * 0.7f
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar with App Badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x331E293B),
                            border = BorderStroke(1.5.dp, TealGlow),
                            modifier = Modifier.size(44.dp)
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

                        // Top-right notification dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFFA855F7), CircleShape)
                        )
                    }
                }
            }

            // 2. Central Hero Graphic & Password Form
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Central Glass Check Card
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color(0x401E293B),
                        border = BorderStroke(1.5.dp, GlassBorder),
                        shadowElevation = 16.dp,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0x4000E5FF), Color(0x207C3AED))
                                        )
                                    )
                            )

                            Canvas(modifier = Modifier.size(36.dp)) {
                                drawCheckmarkPath(color = TealBright, strokeWidth = 5f)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "SECURE YOUR ACCOUNT",
                        color = TealBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title: Create a new password
                    Text(
                        text = "Create a new password",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle
                    Text(
                        text = "Choose a strong password you haven't used before.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // New Password Input Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NEW PASSWORD",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Box(modifier = Modifier.size(20.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLockIcon(color = CyanAccent, strokeWidth = 3f)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Input Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CONFIRM PASSWORD",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Box(modifier = Modifier.size(20.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLockIcon(color = CyanAccent, strokeWidth = 3f)
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // Password Requirements Checklist Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x3300E5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PasswordRequirementRow(text = "At least 8 characters", isMet = hasMinLength)
                            PasswordRequirementRow(text = "Contains number and symbol", isMet = hasNumberAndSymbol)
                            PasswordRequirementRow(text = "Passwords match", isMet = passwordsMatch)
                        }
                    }
                }
            }

            // 3. Update Password Button
            item {
                Button(
                    onClick = {
                        if (!hasMinLength) {
                            localError = "Password must be at least 8 characters long."
                            Toast.makeText(context, localError, Toast.LENGTH_SHORT).show()
                        } else if (!passwordsMatch) {
                            localError = "Passwords do not match."
                            Toast.makeText(context, localError, Toast.LENGTH_SHORT).show()
                        } else {
                            localError = null
                            viewModel.resetPassword(
                                newPass = newPassword,
                                confirmPass = confirmPassword,
                                email = targetEmail,
                                onSuccess = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    onUpdatePasswordSuccess()
                                },
                                onError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = TealBright),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = if (isLoading) "Updating Password..." else "Update Password",
                        color = DarkNavyStart,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordRequirementRow(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = if (isMet) HighTrustGreen.copy(alpha = 0.18f) else Color(0x1F1E293B),
            border = BorderStroke(1.dp, if (isMet) HighTrustGreen else Color(0x33FFFFFF)),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isMet) {
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCheckmarkPath(color = HighTrustGreen, strokeWidth = 2.5f)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(Color(0xFF64748B), CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = if (isMet) Color.White else Color(0xFF94A3B8),
            fontSize = 12.sp,
            fontWeight = if (isMet) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen()
}
