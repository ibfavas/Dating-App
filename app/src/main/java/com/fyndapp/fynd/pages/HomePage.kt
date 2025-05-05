package com.fyndapp.fynd.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.other.Screens

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screens) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screens.Home)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screens.Profile)
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screens.AccountSettings)
    object ContactUs : BottomNavItem("Contact", Icons.Default.Phone, Screens.ContactUs)
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.Home)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Home Page Content")
        }
    }
}