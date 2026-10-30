package com.example.myapplication.data.network.repository

import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.ForgotPasswordRequest
import com.example.myapplication.data.network.model.LoginRequest
import com.example.myapplication.data.network.model.ResetPasswordRequest
import com.example.myapplication.data.network.model.SignUpRequest
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.model.VerifyOtpRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository managing Authentication operations for TruthLens.
 * Encapsulates network communication with both Retrofit and direct OkHttp fallback,
 * and maintains local session state via SessionManager.
 */
class AuthRepository(
    private val sessionManager: SessionManager? = null
) {
    private val apiService = RetrofitClient.apiService

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val request = SignUpRequest(name = name, email = email, password = password)
            val response = try {
                apiService.signUp(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<UserDto?>("auth/signup.php", request)
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

    suspend fun login(
        email: String,
        password: String
    ): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email = email, password = password)
            val response = try {
                apiService.login(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<UserDto>("auth/login.php", request)
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

    suspend fun sendResetCode(
        email: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = ForgotPasswordRequest(email = email)
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
            val request = VerifyOtpRequest(email = email, otp = otp)
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

    suspend fun resetPassword(
        email: String,
        newPassword: String,
        confirmPassword: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = ResetPasswordRequest(
                email = email,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )
            val response = try {
                apiService.resetPassword(request)
            } catch (e: Exception) {
                DirectApiClient.postJson<Unit>("auth/reset_password.php", request)
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

    fun isLoggedIn(): Boolean = sessionManager?.isLoggedIn ?: false

    fun getCurrentUserId(): Int = sessionManager?.currentUserId ?: 0

    fun getCurrentUserName(): String = sessionManager?.currentUserName ?: ""

    fun getCurrentUserEmail(): String = sessionManager?.currentUserEmail ?: ""

    fun logout() {
        sessionManager?.logout()
    }
}
