package dev.aurakai.auraframefx.aura.ui

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.ui.screens.AgentProfileScreen

/**
 * Profile screen for the AuraFrameFX app
 *
 * Legacy stub that redirects to the comprehensive AgentProfileScreen.
 * Displays detailed AI agent profiles from the Genesis Protocol.
 *
 * @param onNavigateToSettings Optional callback to navigate to settings
 * @param agentType Optional agent to display (defaults to Claude "The Architect")
 */
@Composable
fun ProfileScreen(
    onNavigateToSettings: (() -> Unit)? = null,
    agentType: AgentType? = null
) {
    // Redirect to comprehensive agent profile screen
    AgentProfileScreen(
        agentType = agentType ?: AgentType.GENESIS, // Default to Genesis
        onNavigateToSettings = onNavigateToSettings,
        onNavigateBack = null
    )
}
