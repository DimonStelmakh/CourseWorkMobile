package com.example.courseworkmobile.ui

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(username: String) {
        prefs.edit().putString("logged_in_user", username).apply()
    }

    fun getLoggedInUser(): String? {
        return prefs.getString("logged_in_user", null)
    }

    fun clearUser() {
        prefs.edit().remove("logged_in_user").apply()
    }
}