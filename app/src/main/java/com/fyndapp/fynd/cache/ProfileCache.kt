package com.fyndapp.fynd.cache

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ProfileCache {
    private const val PREFS_NAME = "profile_prefs"
    private const val KEY_NAME = "name"
    private const val KEY_DOB = "dob"
    private const val KEY_GENDER = "gender"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_AVATAR = "avatar"

    var name by mutableStateOf("")
    var dob by mutableStateOf("")
    var gender by mutableStateOf("")
    var language by mutableStateOf("")
    var avatar by mutableStateOf("")

    fun loadFromCache(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        name = prefs.getString(KEY_NAME, "") ?: ""
        dob = prefs.getString(KEY_DOB, "") ?: ""
        gender = prefs.getString(KEY_GENDER, "") ?: ""
        language = prefs.getString(KEY_LANGUAGE, "") ?: ""
        avatar = prefs.getString(KEY_AVATAR, "") ?: ""
    }

    fun saveToCache(context: Context, name: String, dob: String, gender: String, language: String, avatar: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_DOB, dob)
            putString(KEY_GENDER, gender)
            putString(KEY_LANGUAGE, language)
            putString(KEY_AVATAR, avatar)
            apply()
        }

        // Update the mutable states
        this.name = name
        this.dob = dob
        this.gender = gender
        this.language = language
        this.avatar = avatar
    }

    fun clearCache(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}