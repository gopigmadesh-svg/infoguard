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
        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_PUSH_NOTIFICATIONS = "push_notifications"
        private const val KEY_FACT_CHECK_REMINDERS = "fact_check_reminders"
        private const val KEY_WEEKLY_SUMMARY = "weekly_summary"
        private const val KEY_PREFERRED_LANGUAGE = "preferred_language"
        private const val KEY_TEXT_SCALE = "text_scale"
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
            } else {
                remove(KEY_USER_BIO)
            }
            val avatar = user.avatarUrl ?: user.profilePicture
            if (!avatar.isNullOrEmpty()) {
                putString(KEY_USER_AVATAR, avatar)
            } else {
                remove(KEY_USER_AVATAR)
            }
            if (!resolvedToken.isNullOrEmpty()) {
                putString(KEY_AUTH_TOKEN, resolvedToken)
                NetworkConfig.currentAuthToken = resolvedToken
            }
        }.commit()
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

    var appTheme: String
        get() = prefs.getString(KEY_APP_THEME, "Dark Cyan") ?: "Dark Cyan"
        set(value) = prefs.edit().putString(KEY_APP_THEME, value).apply()

    var pushNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_PUSH_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_PUSH_NOTIFICATIONS, value).apply()

    var factCheckRemindersEnabled: Boolean
        get() = prefs.getBoolean(KEY_FACT_CHECK_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_FACT_CHECK_REMINDERS, value).apply()

    var weeklySummaryEnabled: Boolean
        get() = prefs.getBoolean(KEY_WEEKLY_SUMMARY, false)
        set(value) = prefs.edit().putBoolean(KEY_WEEKLY_SUMMARY, value).apply()

    var preferredLanguage: String
        get() = prefs.getString(KEY_PREFERRED_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit().putString(KEY_PREFERRED_LANGUAGE, value).apply()

    var textScale: String
        get() = prefs.getString(KEY_TEXT_SCALE, "Default (100%)") ?: "Default (100%)"
        set(value) = prefs.edit().putString(KEY_TEXT_SCALE, value).apply()

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_BIO)
            remove(KEY_USER_AVATAR)
            remove(KEY_AUTH_TOKEN)
        }.commit()
        NetworkConfig.currentAuthToken = null
    }

    fun logout() {
        clearSession()
    }
}
