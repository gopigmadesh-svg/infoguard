package com.example.myapplication.data.network.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.network.model.UserDto

/**
 * Manages local user session and credentials storage using Android SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "truthlens_user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveUser(user: UserDto, token: String? = null) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name.orEmpty())
            putString(KEY_USER_EMAIL, user.email)
            if (!token.isNullOrEmpty()) {
                putString(KEY_AUTH_TOKEN, token)
            }
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

    val authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)

    fun logout() {
        prefs.edit().clear().commit()
    }
}

