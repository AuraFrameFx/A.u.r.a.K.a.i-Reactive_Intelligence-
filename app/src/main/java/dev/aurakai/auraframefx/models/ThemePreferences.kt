package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

/**
 * User theme preferences
 */
@Serializable
data class ThemePreferences(
    val isDarkMode: Boolean = true,
    val primaryColor: Long = 0xFFFF1744,
    val accentColor: Long = 0xFF00BCD4,
    val useDynamicColors: Boolean = false,
    val themeName: String = "cyberpunk"
)

/**
 * Theme configuration for the UI
 */
@Serializable
data class ThemeConfiguration(
    val name: String,
    val displayName: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val backgroundColor: Long,
    val surfaceColor: Long,
    val isDark: Boolean = true
)
