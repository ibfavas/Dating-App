package com.fyndapp.fynd.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fyndapp.BottomNavigationBar
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.other.Screens

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.Profile)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Profile Screen Content")
        }
    }
}