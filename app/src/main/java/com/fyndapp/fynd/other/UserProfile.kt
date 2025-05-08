package com.fyndapp.fynd.other


data class UserProfile(
    val id: String,
    val name: String,
    val age: Int,
    val avatarResId: Int,
    val language: String,
    val matchScore: Int
)