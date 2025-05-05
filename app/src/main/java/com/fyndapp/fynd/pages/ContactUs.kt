package com.fyndapp.fynd.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.other.Screens

@Composable
fun ContactUs(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.ContactUs)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Text("Contact Us Screen Content")
        }
    }
}