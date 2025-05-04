package com.fyndapp.fynd.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.fyndapp.fynd.AuthViewModel

@Composable
fun FAQ(
    modifier: Modifier= Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
){
    Text("FAQ")
}