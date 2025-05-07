package com.fyndapp.fynd.cache

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SettingsCache {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_DARK_THEME = "dark_theme"

    var isDarkTheme by mutableStateOf(false)

    fun loadFromCache(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
    }

    fun saveToCache(context: Context, isDarkTheme: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_DARK_THEME, isDarkTheme)
            apply()
        }

        // Update the mutable state
        this.isDarkTheme = isDarkTheme
    }

    fun clearCache(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}