package com.fyndapp.fynd

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(private val context: Context) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    init {
        loadTheme()
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        saveTheme(_isDarkTheme.value)
    }

    private fun saveTheme(isDark: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            "ThemePreferences",
            Context.MODE_PRIVATE
        ) // Using a separate prefs file
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDark)
        editor.apply()
    }

    private fun loadTheme() {
        val sharedPreferences = context.getSharedPreferences(
            "ThemePreferences",
            Context.MODE_PRIVATE
        ) // Using a separate prefs file
        _isDarkTheme.value = sharedPreferences.getBoolean("isDarkTheme", false)
    }
}
