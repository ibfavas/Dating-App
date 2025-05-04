package com.fyndapp.fynd.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fyndapp.BottomNavigationBar
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