package com.fyndapp.fynd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Define light and dark color schemes
private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF112473),
    secondary = androidx.compose.ui.graphics.Color(0xFF112473),
    background = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color.Black,
    onSurface = androidx.compose.ui.graphics.Color.LightGray,
    tertiary = androidx.compose.ui.graphics.Color(0xFFC0DFFF),
    onTertiary = androidx.compose.ui.graphics.Color.White,
    inversePrimary = androidx.compose.ui.graphics.Color(0xFFFF675C),
    inverseSurface = androidx.compose.ui.graphics.Color.DarkGray
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
    secondary = androidx.compose.ui.graphics.Color(0xFF8BEC8D),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF121212),
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    onSecondary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.DarkGray,
    tertiary = androidx.compose.ui.graphics.Color(0xFF64B6F1),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF232323),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFFF44336),
    inverseSurface = androidx.compose.ui.graphics.Color.LightGray

)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Default to system theme
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}