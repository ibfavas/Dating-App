package com.fyndapp.fynd.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.other.Screens

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screens) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screens.Home)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screens.Profile)
    object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screens.AccountSettings)
    object ContactUs : BottomNavItem("Contact", Icons.Default.Phone, Screens.ContactUs)
    object FAQ : BottomNavItem("FAQ", Icons.Default.Info, Screens.FAQ)
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
        BottomNavItem.Settings,
        BottomNavItem.ContactUs,
        BottomNavItem.FAQ
    )

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 8.dp) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.screen.route)
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.title)
                        },
                        label = {
                            Text(text = item.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Home Page Content")
        }
    }
}
