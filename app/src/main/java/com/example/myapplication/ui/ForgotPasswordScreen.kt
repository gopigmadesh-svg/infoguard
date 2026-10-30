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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import android.util.Patterns
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel? = null,
    onSendCodeClick: (String) -> Unit = {},
    onBackToLoginClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = authViewModel ?: remember {
        val sessionManager = SessionManager(context)
        AuthViewModel(AuthRepository(sessionManager))
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val serverError by viewModel.errorMessage.collectAsState()

    var email by remember { mutableStateOf(viewModel.resetEmail.value.ifEmpty { "alex@example.com" }) }
    var localError by remember { mutableStateOf<String?>(null) }

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

            // 2. Central Hero Graphic & Form Section
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Central Lock Card
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

                            Canvas(modifier = Modifier.size(44.dp)) {
                                drawLockIcon(color = Color.White, strokeWidth = 3.5f)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "ACCOUNT RECOVERY",
                        color = TealBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title: Forgot your password?
                    Text(
                        text = "Forgot your password?",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle
                    Text(
                        text = "Enter the email linked to your account. We'll send a secure verification code.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email Address Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EMAIL ADDRESS",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            singleLine = true,
                            leadingIcon = {
                                Box(modifier = Modifier.size(20.dp)) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawEmailIcon(color = CyanAccent, strokeWidth = 3f)
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
            }

            // 3. CTA Action Buttons & Security Note
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val trimmed = email.trim()
                            if (trimmed.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                                localError = "Please enter a valid email address."
                                Toast.makeText(context, localError, Toast.LENGTH_SHORT).show()
                            } else {
                                localError = null
                                viewModel.sendResetCode(
                                    email = trimmed,
                                    onSuccess = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        onSendCodeClick(trimmed)
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
                            text = if (isLoading) "Sending Code..." else "Send Verification Code",
                            color = DarkNavyStart,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0x261E293B),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clickable { onBackToLoginClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Back to Login",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Security Expiration Note Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
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
                                text = "For your security, verification codes expire after 15 minutes.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}
