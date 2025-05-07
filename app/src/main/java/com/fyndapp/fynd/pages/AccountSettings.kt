package com.fyndapp.fynd.pages

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.ThemeViewModel
import com.fyndapp.fynd.cache.SettingsCache
import com.fyndapp.fynd.other.Screens
import com.fyndapp.fynd.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var doubleBackToExitPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Load settings from cache
    SettingsCache.loadFromCache(context)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = Screens.AccountSettings)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )

                SettingsCard {
                    SettingsItem(
                        iconRes = if (isDarkTheme) R.drawable.ic_night else R.drawable.ic_day,
                        title = "Appearance",
                        subtitle = if (isDarkTheme) "Dark Mode" else "Light Mode",
                        action = {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { checked ->
                                    themeViewModel.toggleTheme()
                                    SettingsCache.saveToCache(context, checked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Policies",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                )

                SettingsCard {
                    Column {
                        SettingsItem(
                            iconRes = R.drawable.ic_terms,
                            title = "Terms of Service",
                            onClick = { /* Handle terms click */ }
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        SettingsItem(
                            iconRes = R.drawable.ic_policy,
                            title = "Privacy Policy",
                            onClick = { /* Handle policy click */ }
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        SettingsItem(
                            iconRes = R.drawable.ic_block,
                            title = "Blocked Accounts",
                            onClick = { /* Handle blocked accounts click */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                )

                SettingsCard {
                    Column {
                        val onLogoutClick = remember { { showLogoutDialog = true } }

                        SettingsItem(
                            iconRes = R.drawable.ic_logout,
                            title = "Log Out",
                            iconTint = MaterialTheme.colorScheme.primary,
                            onClick = onLogoutClick
                        )

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        SettingsItem(
                            iconRes = R.drawable.ic_delete,
                            title = "Delete Account",
                            titleColor = MaterialTheme.colorScheme.inversePrimary,
                            iconTint = MaterialTheme.colorScheme.inversePrimary
                        ) {
                            /* Handle delete account */
                        }
                    }
                }

                Text(
                    text = "App Version 2025.01.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Log Out", color = MaterialTheme.colorScheme.onBackground) },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                authViewModel.logout()
                                navController.navigate(Screens.Login.route) {
                                    popUpTo(Screens.AccountSettings.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Log Out", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                )
            }
        }
    }
    BackHandler {
        if (doubleBackToExitPressedOnce) {
            (context as? Activity)?.finishAffinity()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()

            // Reset the flag after 2 seconds using CoroutineScope
            coroutineScope.launch {
                delay(2000L)
                doubleBackToExitPressedOnce = false
            }
        }
    }
}

// Card Wrapper
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(content = content)
    }
}

// Settings Item UI
@Composable
fun SettingsItem(
    iconRes: Int,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(if (onClick != null) Modifier.noRippleClickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp),
            tint = iconTint
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.offset(y = 1.dp) // 👈 Move it 1dp down
                )
            }
        }

        action?.invoke() ?: if (onClick != null) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        } else {

        }
    }
}

// No Ripple Clickable Modifier
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}