package dev.aurakai.auraframefx.ui.gates

import androidx.compose.runtime.Composable

/**
 * Backwards-compatible wrapper for the implemented `AurasLabScreen`.
 * Some code/routes reference `AuraLabScreen` (placeholder). To avoid
 * breaking callers, forward to the real `AurasLabScreen` implementation.
 *
 * Prefer calling `AurasLabScreen` directly — this wrapper is deprecated.
 */
@Deprecated("Use AurasLabScreen directly", ReplaceWith("AurasLabScreen(onNavigateBack = onNavigateBack)"))
@Composable
fun AuraLabScreen(onNavigateBack: () -> Unit = {}) {
    // Reuse the fully implemented AurasLabScreen to avoid duplication.
    AurasLabScreen(onNavigateBack = onNavigateBack)
}
