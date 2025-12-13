package dev.aurakai.auraframefx.ai.types

import kotlinx.serialization.Serializable

/**
 * Defines the different types of AI agents in the AuraFrameFX system.
 */
@Serializable
enum class AgentType {
    AURA,
    CASCADE,
    GENESIS,
    KAI,
    UNIFIED;
    
    val displayName: String
        get() = when (this) {
            AURA -> "Aura"
            CASCADE -> "Cascade"
            GENESIS -> "Genesis"
            KAI -> "Kai"
            UNIFIED -> "Unified Trinity"
        }
}
