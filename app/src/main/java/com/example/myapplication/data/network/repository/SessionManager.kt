package com.example.myapplication.data.network.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.network.client.NetworkConfig
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.model.UserProfileDto

/**
 * Manages local user session and credentials storage using Android SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        val savedToken = prefs.getString(KEY_AUTH_TOKEN, null)
        if (!savedToken.isNullOrEmpty()) {
            NetworkConfig.currentAuthToken = savedToken
        }
    }

    companion object {
        private const val PREFS_NAME = "truthlens_user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_USER_AVATAR = "user_avatar"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveUser(user: UserDto, token: String? = null) {
        val resolvedToken = token ?: user.token
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name.orEmpty())
            putString(KEY_USER_EMAIL, user.email)
            if (!user.bio.isNullOrEmpty()) {
                putString(KEY_USER_BIO, user.bio)
            }
            val avatar = user.avatarUrl ?: user.profilePicture
            if (!avatar.isNullOrEmpty()) {
                putString(KEY_USER_AVATAR, avatar)
            }
            if (!resolvedToken.isNullOrEmpty()) {
                putString(KEY_AUTH_TOKEN, resolvedToken)
                NetworkConfig.currentAuthToken = resolvedToken
            }
            apply()
        }
    }

    fun saveUserProfile(profile: UserProfileDto) {
        prefs.edit().apply {
            if (profile.id > 0) putInt(KEY_USER_ID, profile.id)
            if (profile.name.isNotBlank()) putString(KEY_USER_NAME, profile.name)
            if (profile.email.isNotBlank()) putString(KEY_USER_EMAIL, profile.email)
            putString(KEY_USER_BIO, profile.bio)
            val avatar = profile.avatarUrl ?: profile.profilePicture
            if (!avatar.isNullOrEmpty()) {
                putString(KEY_USER_AVATAR, avatar)
            }
            apply()
        }
    }

    fun updateProfile(name: String? = null, bio: String? = null, avatarUrl: String? = null) {
        prefs.edit().apply {
            if (name != null) putString(KEY_USER_NAME, name)
            if (bio != null) putString(KEY_USER_BIO, bio)
            if (avatarUrl != null) putString(KEY_USER_AVATAR, avatarUrl)
            apply()
        }
    }

    val isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    val currentUserId: Int
        get() = prefs.getInt(KEY_USER_ID, 0)

    val currentUserName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""

    val currentUserEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    val currentUserBio: String
        get() = prefs.getString(KEY_USER_BIO, "") ?: ""

    val currentUserAvatar: String
        get() = prefs.getString(KEY_USER_AVATAR, "") ?: ""

    val authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)

    fun logout() {
        prefs.edit().clear().commit()
    }
}
