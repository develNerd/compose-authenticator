package org.com.composeauthenticator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4F46E5),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9CA3AF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6B7280),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF10B981),
    onTertiary = Color.White,
    error = Color(0xFFEF4444),
    onError = Color.White,
    background = Color(0xFF111827),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFE5E7EB),
    outline = Color(0xFF6B7280)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF6366F1),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF6B7280),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9CA3AF),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    error = Color(0xFFDC2626),
    onError = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    outline = Color(0xFFD1D5DB)
)

@Composable
fun ComposeAuthenticatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}