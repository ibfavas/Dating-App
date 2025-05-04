package com.fyndapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fyndapp.fynd.other.Screens
import com.fyndapp.fynd.pages.BottomNavItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentScreen: Screens,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
        BottomNavItem.Settings,
        BottomNavItem.ContactUs
    )

    val selectedItem = items.indexOfFirst { it.screen == currentScreen }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color(0xFFF9E2FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == index
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .noRippleClickable {
                            if (!isSelected) {
                                navController.navigate(item.screen.route) {
                                    // This ensures smooth transition between screens
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isSelected) Color(0xFF9C27B0) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color.White else Color(0xFF9C27B0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to remove ripple effect
@SuppressLint("SuspiciousModifierThen")
@Composable
private inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    this.then(
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick() }
        )
    )