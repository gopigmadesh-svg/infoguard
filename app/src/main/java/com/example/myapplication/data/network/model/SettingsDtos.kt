package com.example.myapplication.data.network.model

import com.google.gson.annotations.SerializedName

data class UserSettingsDto(
    @SerializedName("theme")
    val theme: String = "dark",

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @SerializedName("language")
    val language: String = "English (US)",

    @SerializedName("interests")
    val interests: List<String> = listOf("SCIENCE", "TECHNOLOGY", "HEALTH")
)

data class UpdateSettingsRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("theme")
    val theme: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("interests")
    val interests: List<String>? = null
)

data class UpdateProfileRequest(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("profile_picture")
    val profilePicture: String? = null,

    @SerializedName("interests")
    val interests: List<String>? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean? = null
)

data class UserProfileDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "Alex Morgan",

    @SerializedName("email")
    val email: String = "alex.morgan@example.com",

    @SerializedName("bio")
    val bio: String = "Senior Digital Media Researcher",

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("profile_picture")
    val profilePicture: String? = null,

    @SerializedName("account_type")
    val accountType: String = "Standard Account • Active",

    @SerializedName("checks_count")
    val checksCount: Int = 0,

    @SerializedName("saved_count")
    val savedCount: Int = 0,

    @SerializedName("topics_count")
    val topicsCount: Int = 0,

    @SerializedName("interests")
    val interests: List<String> = listOf("SCIENCE", "TECHNOLOGY", "HEALTH", "CURRENT AFFAIRS"),

    @SerializedName("language")
    val language: String = "English (US)",

    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @SerializedName("created_at")
    val createdAt: String? = null
)
