package com.example.ventasarriendos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Orange40,
    secondary = Orange30,
    tertiary = Pink40,
    background = Color.Black,
    surface = Color(0xFF121212)
)

private val LightColorScheme = lightColorScheme(
    primary = Orange80,
    onPrimary = White,
    primaryContainer = Orange50,
    onPrimaryContainer = White,

    secondary = Orange50,
    onSecondary = White,
    secondaryContainer = Orange20,
    onSecondaryContainer = Color.Black,

    tertiary = Pink80,
    onTertiary = Color.Black,

    background = White,
    onBackground = Color.Black,

    surface = White,
    onSurface = Color.Black,

    surfaceVariant = LightGray,
    onSurfaceVariant = Color.Black
)

@Composable
fun VentasArriendosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}