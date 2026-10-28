package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.ForgotPasswordRequest
import com.example.myapplication.data.network.model.LoginRequest
import com.example.myapplication.data.network.model.SignUpRequest
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.model.VerifyOtpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository managing Authentication operations for TruthLens.
 * Encapsulates network communication and error handling.
 */
class AuthRepository(
    private val sessionManager: SessionManager? = null
) {
    private val apiService = RetrofitClient.apiService

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<UserDto?> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.signUp(SignUpRequest(name = name, email = email, password = password))
            } catch (e: Exception) {
                // Direct fallback
                DirectApiClient.postJson<UserDto?>("auth/signup.php", SignUpRequest(name, email, password))
            }

            if (response.success) {
                val user = response.data
                if (user != null && sessionManager != null) {
                    sessionManager.saveUser(user)
                }
                Result.success(user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = try {
                apiService.login(LoginRequest(email = email, password = password))
            } catch (e: Exception) {
                // Direct fallback
                DirectApiClient.postJson<UserDto>("auth/login.php", LoginRequest(email, password))
            }

            if (response.success && response.data != null) {
                val user = response.data
                sessionManager?.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = ForgotPasswordRequest(email, newPassword, confirmPassword)
            val response = try {
                apiService.forgotPassword(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("auth/forgot_password.php", request)
            }

            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(
        email: String,
        otp: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = VerifyOtpRequest(email, otp)
            val response = try {
                apiService.verifyOtp(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("auth/verify_otp.php", request)
            }

            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

