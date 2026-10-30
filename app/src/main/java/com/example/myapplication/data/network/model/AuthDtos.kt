package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("new_password")
    val newPassword: String? = null,

    @SerializedName("confirm_password")
    val confirmPassword: String? = null
)

data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("otp")
    val otp: String
)

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("new_password")
    val newPassword: String,

    @SerializedName("confirm_password")
    val confirmPassword: String
)

data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("profile_picture")
    val profilePicture: String? = null,

    @SerializedName("account_type")
    val accountType: String? = null,

    @SerializedName("checks_count")
    val checksCount: Int? = null,

    @SerializedName("saved_count")
    val savedCount: Int? = null,

    @SerializedName("topics_count")
    val topicsCount: Int? = null,

    @SerializedName("interests")
    val interests: List<String>? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)
