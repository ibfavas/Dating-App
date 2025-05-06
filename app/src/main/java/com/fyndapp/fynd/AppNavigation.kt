package com.fyndapp.fynd

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
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

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
        modifier = modifier
    ) {
        addSplashScreen(navController, authViewModel)
        addLoginScreen(navController, authViewModel)
        addSelectGenderScreen(navController, authViewModel)
        addHomeScreen(navController, authViewModel)
        addProfileScreen(navController, authViewModel)
        addAccountSettingsScreen(navController, authViewModel, themeViewModel)
        addContactUsScreen(navController, authViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addSplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(
        route = Screens.Splash.route,
        // Add any arguments if needed
    ) {
        SplashScreen(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addLoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(Screens.Login.route) {
        LoginScreen(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addSelectGenderScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(Screens.SelectGender.route) {
        SelectGenderScreen(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

private fun NavGraphBuilder.addHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(
        route = Screens.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        }
    ) {
        HomePage(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(
        route = Screens.Profile.route,
        enterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        }
    ) {
        Profile(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addAccountSettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    composable(
        route = Screens.AccountSettings.route,
        enterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        }
    ) {
        AccountSettings(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel,
            themeViewModel = themeViewModel
        )
    }
}

private fun NavGraphBuilder.addContactUsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    composable(
        route = Screens.ContactUs.route,
        enterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        }
    ) {
        ContactUs(
            modifier = Modifier,
            navController = navController,
            authViewModel = authViewModel
        )
    }
}