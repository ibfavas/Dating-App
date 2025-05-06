package com.fyndapp.fynd.ui

import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.R
import com.fyndapp.fynd.other.Screens
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val scale = remember { Animatable(0f) }
    val authState by authViewModel.authState.collectAsState()
    val user = authState.user
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 400,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        delay(2000L)

        coroutineScope.launch {
            if (user != null) {
                try {
                    Firebase.firestore.collection("users").document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists() && document.getString("gender") != null) {
                                navController.navigate(Screens.Home.route) {
                                    popUpTo(Screens.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screens.SelectGender.route) {
                                    popUpTo(Screens.Splash.route) { inclusive = true }
                                }
                            }
                        }
                        .addOnFailureListener {
                            navController.navigate(Screens.Login.route) {
                                popUpTo(Screens.Splash.route) { inclusive = true }
                            }
                        }
                } catch (e: Exception) {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                }
            } else {
                navController.navigate(Screens.Login.route) {
                    popUpTo(Screens.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.fynd_ai),
            contentDescription = "Splash Logo",
            modifier = Modifier.fillMaxWidth()
                .height(600.dp)
        )
    }
}