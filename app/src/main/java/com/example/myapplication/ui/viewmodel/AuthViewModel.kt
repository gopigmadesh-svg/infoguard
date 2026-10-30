package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing UI state for Authentication operations.
 */
sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val message: String, val user: UserDto? = null) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

/**
 * MVVM ViewModel managing authentication state and orchestrating auth operations
 * between the UI Views and the AuthRepository backend.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    // Holds the email during forgot password -> OTP -> reset password flow
    val resetEmail = MutableStateFlow("")

    val isLoggedIn: Boolean
        get() = repository.isLoggedIn()

    val currentUserName: String
        get() = repository.getCurrentUserName()

    val currentUserEmail: String
        get() = repository.getCurrentUserEmail()

    fun clearError() {
        _errorMessage.value = null
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun setResetPasswordEmail(email: String) {
        resetEmail.value = email.trim()
    }

    /**
     * Authenticate user with Email & Password
     */
    fun login(
        email: String,
        pass: String,
        onSuccess: (UserDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()

        if (trimmedEmail.isEmpty()) {
            val err = "Please enter your email address."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (trimmedPass.isEmpty()) {
            val err = "Please enter your password."
            _errorMessage.value = err
            onError(err)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.login(trimmedEmail, trimmedPass)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success("Login successful", user)
                onSuccess(user)
            }.onFailure { ex ->
                val errorMsg = ex.message ?: "Authentication failed. Please check your credentials."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Register a new user account
     */
    fun signUp(
        name: String,
        email: String,
        pass: String,
        onSuccess: (UserDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()

        if (trimmedName.isEmpty()) {
            val err = "Please enter your full name."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (trimmedEmail.isEmpty()) {
            val err = "Please enter your email address."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (trimmedPass.length < 8) {
            val err = "Password must be at least 8 characters long."
            _errorMessage.value = err
            onError(err)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.signUp(trimmedName, trimmedEmail, trimmedPass)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success("Sign up successful", user)
                onSuccess(user)
            }.onFailure { ex ->
                val errorMsg = ex.message ?: "Registration failed. Please try again."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Request a password reset verification code
     */
    fun sendResetCode(
        email: String,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            val err = "Please enter your email address."
            _errorMessage.value = err
            onError(err)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.sendResetCode(trimmedEmail)
            _isLoading.value = false

            result.onSuccess { message ->
                resetEmail.value = trimmedEmail
                _successMessage.value = message
                _uiState.value = AuthUiState.Success(message)
                onSuccess(message)
            }.onFailure { ex ->
                val errorMsg = ex.message ?: "Could not send verification code. Check email address."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Verify the 6-digit OTP code received by the user
     */
    fun verifyOtp(
        email: String,
        otp: String,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val targetEmail = email.ifEmpty { resetEmail.value }.trim()
        val trimmedOtp = otp.trim()

        if (targetEmail.isEmpty()) {
            val err = "Email address missing for verification."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (trimmedOtp.length != 6) {
            val err = "Please enter the complete 6-digit verification code."
            _errorMessage.value = err
            onError(err)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.verifyOtp(targetEmail, trimmedOtp)
            _isLoading.value = false

            result.onSuccess { msg ->
                _successMessage.value = msg
                _uiState.value = AuthUiState.Success(msg)
                onSuccess(msg)
            }.onFailure { ex ->
                val errorMsg = ex.message ?: "Invalid OTP code. Please try again."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Reset and update user password with confirmation
     */
    fun resetPassword(
        newPass: String,
        confirmPass: String,
        email: String = "",
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val targetEmail = email.ifEmpty { resetEmail.value }.trim()

        if (targetEmail.isEmpty()) {
            val err = "Session email missing. Please start password reset again."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (newPass.length < 6) {
            val err = "Password must be at least 6 characters long."
            _errorMessage.value = err
            onError(err)
            return
        }

        if (newPass != confirmPass) {
            val err = "New password and confirmation password do not match."
            _errorMessage.value = err
            onError(err)
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = repository.resetPassword(targetEmail, newPass, confirmPass)
            _isLoading.value = false

            result.onSuccess { msg ->
                _successMessage.value = msg
                _uiState.value = AuthUiState.Success(msg)
                onSuccess(msg)
            }.onFailure { ex ->
                val errorMsg = ex.message ?: "Password reset failed. Please try again."
                _errorMessage.value = errorMsg
                _uiState.value = AuthUiState.Error(errorMsg)
                onError(errorMsg)
            }
        }
    }

    /**
     * Log user out and clear local session
     */
    fun logout(onSuccess: () -> Unit = {}) {
        repository.logout()
        _currentUser.value = null
        _errorMessage.value = null
        _successMessage.value = null
        _isLoading.value = false
        resetEmail.value = ""
        _uiState.value = AuthUiState.Idle
        onSuccess()
    }
}

