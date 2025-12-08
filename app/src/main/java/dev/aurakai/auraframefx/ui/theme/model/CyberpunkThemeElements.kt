package dev.aurakai.auraframefx.ui.theme.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.aurakai.auraframefx.ui.AppTypography
import dev.aurakai.auraframefx.ui.ErrorColor
import dev.aurakai.auraframefx.ui.NeonPurpleLegacy
import dev.aurakai.auraframefx.ui.OnSurface

// Lightweight theme helpers for cyberpunk-styled text colors and mapped text styles.
// This file intentionally keeps only presentation helpers (no animation/particle code).

sealed class CyberpunkTextColor(val color: Color) {
    object Primary : CyberpunkTextColor(OnSurface)
    object Secondary : CyberpunkTextColor(color = NeonPurpleLegacy)
    object Warning : CyberpunkTextColor(ErrorColor)
    object White : CyberpunkTextColor(Color.White)
}

sealed class CyberpunkTextStyle(val textStyle: TextStyle) {
    object Label : CyberpunkTextStyle(AppTypography.labelMedium)
    object Body : CyberpunkTextStyle(AppTypography.bodyMedium)
    object Emphasis : CyberpunkTextStyle(AppTypography.titleMedium)
    object Glitch : CyberpunkTextStyle(AppTypography.displaySmall)
}
