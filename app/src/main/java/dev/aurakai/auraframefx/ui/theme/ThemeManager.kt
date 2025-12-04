package dev.aurakai.auraframefx.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import dev.aurakai.auraframefx.customization.CustomizationPreferences

/**
 * ThemeManager binds application-wide theme from CustomizationPreferences.
 * NOTE: Per request, app-wide theme is applied to Aura-only contexts.
 */
object ThemeManager {
    data class ThemeState(
        val dark: Boolean,
        val name: String,
        val accent: String
    )

    @Composable
    fun auraThemeState(context: Context): State<ThemeState> {
        val darkFlow = remember { CustomizationPreferences.themeDarkFlow(context) }
        val nameFlow = remember { CustomizationPreferences.themeNameFlow(context) }
        val accentFlow = remember { CustomizationPreferences.themeAccentFlow(context) }
        val dark = darkFlow.collectAsState(initial = true)
        val name = nameFlow.collectAsState(initial = "CyberGlow")
        val accent = accentFlow.collectAsState(initial = "NeonBlue")
        return remember(dark.value, name.value, accent.value) {
            object : State<ThemeState> {
                override val value: ThemeState = ThemeState(dark.value, name.value, accent.value)
            }
        }
    }
}

