package com.fyndapp.fynd.other

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screens) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screens.Home)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screens.Profile)
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screens.AccountSettings)
}