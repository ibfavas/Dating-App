package com.fyndapp.fynd.pages

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.other.Screens
import kotlinx.coroutines.*

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screens) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screens.Home)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screens.Profile)
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screens.AccountSettings)
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var doubleBackToExitPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.Home)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Home Page Content")
        }
    }

    BackHandler {
        if (doubleBackToExitPressedOnce) {
            (context as? Activity)?.finishAffinity()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()

            // Reset the flag after 2 seconds using CoroutineScope
            coroutineScope.launch {
                delay(2000L)
                doubleBackToExitPressedOnce = false
            }
        }
    }
}