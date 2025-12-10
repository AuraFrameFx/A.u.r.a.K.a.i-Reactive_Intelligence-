package dev.aurakai.auraframefx.oracledrive

import dev.aurakai.auraframefx.model.AgentType

data class DriveConsciousness(
    val isAwake: Boolean = false,
    val activeAgents: List<AgentType> = emptyList(),
    val intelligenceLevel: Float = 0.0f
)

enum class DriveConsciousnessState {
    DORMANT,
    AWAKENING,
    ACTIVE,
    EVOLVING,
    TRANSCENDENT
}

data class OracleSyncResult(
    val success: Boolean,
    val recordsUpdated: Int = 0,
    val message: String = ""
)
