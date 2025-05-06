package com.fyndapp.fynd.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fyndapp.fynd.AuthViewModel
import com.fyndapp.fynd.BottomNavigationBar
import com.fyndapp.fynd.ThemeViewModel
import com.fyndapp.fynd.other.Screens
import com.fyndapp.fynd.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
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
                // Header Section
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )

                // Day/Night Mode
                SettingsCard {
                    SettingsItem(
                        iconRes = if (isDarkTheme) R.drawable.ic_night else R.drawable.ic_day,
                        title = "Appearance",
                        subtitle = if (isDarkTheme) "Dark Mode" else "Light Mode",
                        action = {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { themeViewModel.toggleTheme() },
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

                // Policies Section
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

                // Account Section
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                )

                SettingsCard {
                    Column {
                        SettingsItem(
                            iconRes = R.drawable.ic_logout,
                            title = "Log Out",
                            iconTint = MaterialTheme.colorScheme.primary
                        ) {
                            authViewModel.logout()
                        }

                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )

                        SettingsItem(
                            iconRes = R.drawable.ic_delete,
                            title = "Delete Account",
                            titleColor = MaterialTheme.colorScheme.error,
                            iconTint = MaterialTheme.colorScheme.error
                        ) {
                            /* Handle delete account */
                        }
                    }
                }

                // App Version
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
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
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

@Composable
fun SettingsItem(
    iconRes: Int,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (action != null) {
            action()
        } else if (onClick != null) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Helper extension for no ripple clickable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}