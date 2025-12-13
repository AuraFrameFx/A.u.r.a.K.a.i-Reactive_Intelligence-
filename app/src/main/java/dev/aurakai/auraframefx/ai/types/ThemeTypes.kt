package dev.aurakai.auraframefx.ai.types

import kotlinx.serialization.Serializable

/**
 * User preferences for theme generation
 */
@Serializable
data class ThemePreferences(
    val primaryColor: String? = null,
    val style: String? = null,
    val mood: String? = null,
    val animationLevel: String? = null,
    val isDarkMode: Boolean = true
)

/**
 * Complete theme configuration
 */
@Serializable
data class ThemeConfiguration(
    val id: String,
    val name: String,
    val primaryColor: String,
    val secondaryColor: String,
    val accentColor: String,
    val backgroundColor: String,
    val textColor: String,
    val style: String,
    val mood: String,
    val animationLevel: String = "medium",
    val isDarkMode: Boolean = true,
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
