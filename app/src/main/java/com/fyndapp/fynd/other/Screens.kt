package com.fyndapp.fynd.other

sealed class Screens(val route: String) {
    object Splash : Screens("splash_screen")
    object Login : Screens("login")
    object SelectGender : Screens("select_gender")
    object Home : Screens("home")
    object Profile : Screens("profile")
    object AccountSettings : Screens("acc settings")
    object FAQ : Screens("faq")
    object ContactUs : Screens("contactus")
}
