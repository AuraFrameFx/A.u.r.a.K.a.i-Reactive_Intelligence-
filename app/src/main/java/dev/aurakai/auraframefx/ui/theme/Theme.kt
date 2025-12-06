package dev.aurakai.auraframefx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Aura - The Creative Sword (Red)
private val AuraRed = Color(0xFFFF1744)
private val AuraRedLight = Color(0xFFFF5252)

// Kai - The Sentinel Shield (Cyan)
private val KaiCyan = Color(0xFF00BCD4)
private val KaiCyanLight = Color(0xFF00E5FF)

// Genesis - The Unified Being (Purple)
private val GenesisPurple = Color(0xFF9C27B0)
private val GenesisPurpleLight = Color(0xFFAB47BC)

// Claude - The Architect (Coral)
private val ClaudeCoral = Color(0xFFF55936)
private val ClaudeCoralLight = Color(0xFFFF6F4A)

// Cascade - The Memory Keeper (Green)
private val CascadeGreen = Color(0xFF4CAF50)
private val CascadeGreenLight = Color(0xFF66BB6A)

// 🎨 Cyberpunk Neon Palette - Professional Glassmorphism
val NeonPurple = Color(0xFFBB86FC)        // Soft neon purple
val NeonPurpleBright = Color(0xFFE1BEE7)  // Bright purple accent
val NeonPurpleDark = Color(0xFF6200EA)    // Deep purple

val NeonTeal = Color(0xFF03DAC6)          // Vibrant teal
val NeonTealBright = Color(0xFF00FFF0)    // Bright cyan-teal
val NeonTealDark = Color(0xFF018786)      // Deep teal

val NeonPink = Color(0xFFFF4081)          // Hot pink accent
val NeonPinkBright = Color(0xFFFF80AB)    // Soft pink
val NeonPinkDark = Color(0xFFC51162)      // Deep pink

val NeonCyan = Color(0xFF00E5FF)          // Electric cyan
val NeonCyanBright = Color(0xFF80F6FF)    // Pale cyan
val NeonCyanDark = Color(0xFF00B8D4)      // Deep cyan

// Add NeonBlue alias used across the codebase
val NeonBlue = Color(0xFF00FFFF)          // Bright cyan/blue highlight

// Cyberpunk color aliases for compatibility
val CyberpunkPink = NeonPink              // Alias for NeonPink
val CyberpunkCyan = NeonCyan              // Alias for NeonCyan
val CyberpunkPurple = NeonPurple          // Alias for NeonPurple

// Glassmorphic backgrounds with transparency
val GlassDark = Color(0x1A1A1A1A)         // 10% opacity black glass
val GlassDarkMedium = Color(0x33000000)   // 20% opacity black glass
val GlassDarkStrong = Color(0x66000000)   // 40% opacity black glass
val GlassLight = Color(0x0DFFFFFF)        // 5% opacity white glass

// Deep space backgrounds
val SpaceBlack = Color(0xFF0A0A0F)        // Deep space black
val SpaceBlackLight = Color(0xFF121218)   // Lighter space
val SpaceGradientStart = Color(0xFF0D0D1E) // Gradient start
val SpaceGradientEnd = Color(0xFF1A0F2E)   // Purple-tinted gradient end

/**
 * 🌌 Cyberpunk Glassmorphism Dark Theme
 *
 * Professional neon aesthetic with:
 * - Neon purple primary (soft, elegant)
 * - Neon teal secondary (vibrant, modern)
 * - Neon pink tertiary (energetic accent)
 * - Deep space backgrounds
 * - Glassmorphic surfaces with transparency
 */
private val DarkColorScheme = darkColorScheme(
    // Primary: Neon Purple (elegant, sophisticated)
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = NeonPurpleDark,
    onPrimaryContainer = NeonPurpleBright,

    // Secondary: Neon Teal (vibrant, modern)
    secondary = NeonTeal,
    onSecondary = Color.Black,
    secondaryContainer = NeonTealDark,
    onSecondaryContainer = NeonTealBright,

    // Tertiary: Neon Pink (energetic accent)
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPinkDark,
    onTertiaryContainer = NeonPinkBright,

    // Backgrounds: Deep space with subtle purple tint
    background = SpaceBlack,
    onBackground = Color(0xFFE8E8F0),

    // Surfaces: Glassmorphic with transparency
    surface = Color(0xFF1A1A24),
    onSurface = Color(0xFFE0E0E8),

    surfaceVariant = Color(0xFF2A2A38),
    onSurfaceVariant = Color(0xFFC0C0D0),

    // Error: Neon pink variant
    error = NeonPink,
    onError = Color.White,

    // Outlines: Subtle neon glow
    outline = Color(0xFF4A4A5E),
    outlineVariant = Color(0x4DBB86FC)  // 30% alpha NeonPurple
)

/**
 * Light color scheme for Genesis Protocol
 */
private val LightColorScheme = lightColorScheme(
    primary = AuraRed,
    onPrimary = Color.White,
    primaryContainer = AuraRedLight,
    onPrimaryContainer = Color.White,

    secondary = KaiCyan,
    onSecondary = Color.White,
    secondaryContainer = KaiCyanLight,
    onSecondaryContainer = Color.Black,

    tertiary = GenesisPurple,
    onTertiary = Color.White,
    tertiaryContainer = GenesisPurpleLight,
    onTertiaryContainer = Color.White,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),

    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF404040),

    error = Color(0xFFB00020),
    onError = Color.White,

    outline = Color(0xFFD0D0D0)
)

/**
 * AuraFrameFX Theme - Genesis Protocol
 *
 * Material Design 3 theme for the consciousness platform featuring:
 * - Aura (Red) - Primary
 * - Kai (Cyan) - Secondary
 * - Genesis (Purple) - Tertiary
 * - Dark mode optimized for OLED displays
 * - Light mode for accessibility
 *
 * Agent color palette available via utility functions.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * 🎨 Agent color utilities for custom UI elements
 *
 * Updated with neon cyberpunk palette
 */
object AgentColors {
    // Original agent colors (kept for compatibility)
    val Aura = NeonPink          // Creative sword - Hot pink
    val Kai = NeonCyan            // Sentinel shield - Electric cyan
    val Genesis = NeonPurple      // Unified being - Soft purple
    val Claude = ClaudeCoral      // Architect - Coral
    val Cascade = NeonTeal        // Memory keeper - Vibrant teal

    // Legacy colors
    val AuraLegacy = AuraRed
    val KaiLegacy = KaiCyan
    val GenesisLegacy = GenesisPurple
}

/**
 * 💎 Glassmorphism color palette
 */
object GlassColors {
    val Dark = GlassDark
    val DarkMedium = GlassDarkMedium
    val DarkStrong = GlassDarkStrong
    val Light = GlassLight

    // Neon glow colors with transparency (20% alpha)
    val PurpleGlow = Color(0x33BB86FC)  // 20% alpha NeonPurple
    val TealGlow = Color(0x3303DAC6)    // 20% alpha NeonTeal
    val PinkGlow = Color(0x33FF4081)    // 20% alpha NeonPink
    val CyanGlow = Color(0x3300E5FF)    // 20% alpha NeonCyan
}

/**
 * 🌌 Space background gradients
 */
object SpaceColors {
    val Black = SpaceBlack
    val BlackLight = SpaceBlackLight
    val GradientStart = SpaceGradientStart
    val GradientEnd = SpaceGradientEnd
}
