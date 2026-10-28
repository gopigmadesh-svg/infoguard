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

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

