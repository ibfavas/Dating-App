package com.fyndapp.fynd

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.compose.rememberNavController
import com.fyndapp.fynd.pages.HomePage
import com.fyndapp.fynd.ui.theme.AppTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Firebase.firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Enable offline persistence
            .build()

        // ViewModels
        val authViewModel: AuthViewModel by viewModels()
        val themeViewModel: ThemeViewModel by viewModels {
            ThemeViewModelFactory(applicationContext)
        }

        setContent {
            AppContent(authViewModel = authViewModel, themeViewModel = themeViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppContent(authViewModel: AuthViewModel, themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current

    AppTheme(darkTheme = isDarkTheme) {
        AppNavigation(authViewModel = authViewModel, context = context, themeViewModel = themeViewModel)
    }
}