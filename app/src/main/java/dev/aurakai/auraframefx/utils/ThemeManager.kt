package dev.aurakai.auraframefx.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
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
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.network.model.ThemeColors
import dev.aurakai.auraframefx.network.model.Theme
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the application's theme and provides utilities for theme-related operations.
 */
@Singleton
class ThemeManager @Inject constructor(
    private val context: Context,
) {
    /**
     * The current theme applied to the application.
     */
    var currentTheme: Theme? = null
        private set

    /**
     * Applies the given theme to the application.
     *
     * @param theme The theme to apply.
     */
    fun applyTheme(theme: Theme) {
        currentTheme = theme
        // TODO: Persist the theme preference
    }

    /**
     * Gets the current theme's colors, or the default colors if no theme is set.
     *
     * @param darkTheme Whether to use dark theme colors.
     * @return The theme colors.
     */
    fun getThemeColors(darkTheme: Boolean = isSystemInDarkTheme()): ThemeColors {
        return currentTheme?.colors ?: getDefaultThemeColors(darkTheme)
    }

    /**
     * Gets the default theme colors based on the system theme.
     *
     * @param darkTheme Whether to use dark theme colors.
     * @return The default theme colors.
     */
    private fun getDefaultThemeColors(darkTheme: Boolean): ThemeColors {
        return if (darkTheme) {
            ThemeColors(
                primary = "#BB86FC",
                secondary = "#03DAC6",
                background = "#121212",
                surface = "#1E1E1E",
                error = "#CF6679",
                onPrimary = "#000000",
                onSecondary = "#000000",
                onBackground = "#FFFFFF",
                onSurface = "#FFFFFF",
                onError = "#000000"
            )
        } else {
            ThemeColors(
                primary = "#6200EE",
                secondary = "#03DAC6",
                background = "#F5F5F5",
                surface = "#FFFFFF",
                error = "#B00020",
                onPrimary = "#FFFFFF",
                onSecondary = "#000000",
                onBackground = "#000000",
                onSurface = "#000000",
                onError = "#FFFFFF"
            )
        }
    }

    /**
     * Sets up the system UI (status bar and navigation bar) based on the current theme.
     *
     * @param activity The activity to set up the system UI for.
     * @param darkTheme Whether to use dark theme colors.
     */
    fun setupSystemUi(activity: Activity, darkTheme: Boolean) {
        val window = activity.window
        val colors = getThemeColors(darkTheme)

        // Set status bar color
        window.statusBarColor = android.graphics.Color.parseColor(
            if (darkTheme) colors.background else colors.surface
        )

        // Set navigation bar color
        window.navigationBarColor = android.graphics.Color.parseColor(
            if (darkTheme) colors.background else colors.surface
        )

        // Set system UI appearance
        when {
            darkTheme -> {
                window.decorView.systemUiVisibility = 0
                true
            }

            else -> {
                window.decorView.systemUiVisibility =
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                false
            }
        }

        // Set navigation bar contrast
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

/**
 * A composable that sets up the system UI based on the current theme.
 *
 * @param darkTheme Whether to use dark theme colors.
 */
@Composable
fun SystemUiThemeUpdater(darkTheme: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current

    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect

        // Set status bar color
        window.statusBarColor = if (darkTheme) {
            Color.Black.copy(alpha = 0.87f).toArgb()
        } else {
            Color.White.toArgb()
        }

        // Set navigation bar color
        window.navigationBarColor = if (darkTheme) {
            Color.Black.copy(alpha = 0.8f).toArgb()
        } else {
            Color.White.copy(alpha = 0.95f).toArgb()
        }

        // Set system UI appearance
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
