package com.fyndapp.fynd

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fyndapp.fynd.other.Screens
import com.fyndapp.fynd.pages.AccountSettings
import com.fyndapp.fynd.pages.ContactUs
import com.fyndapp.fynd.pages.HomePage
import com.fyndapp.fynd.pages.Profile
import com.fyndapp.fynd.ui.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    context: Context,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.Splash.route) {

        composable(Screens.Splash.route) {
            SplashScreen(modifier, navController, authViewModel)
        }

        composable(Screens.Login.route) {
            LoginScreen(modifier, navController, authViewModel)
        }

        composable(Screens.SelectGender.route) {
            SelectGenderScreen(modifier, navController, authViewModel)
        }

        composable(Screens.Home.route) {
            HomePage(modifier, navController, authViewModel)
        }

        composable(Screens.Profile.route) {
            Profile(modifier, navController, authViewModel)
        }

        composable(Screens.AccountSettings.route) {
            AccountSettings(modifier, navController, authViewModel, themeViewModel)
        }
        composable(Screens.ContactUs.route) {
            ContactUs(modifier,navController,authViewModel)
        }
    }
}
