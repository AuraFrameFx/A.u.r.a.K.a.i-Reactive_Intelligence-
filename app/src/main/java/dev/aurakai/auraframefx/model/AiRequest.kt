package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class AiRequest(
    val query: String,
    val type: String? = null, // Added type back, made nullable
    val context: Map<String, String>? = null,
    val data: Map<String, String>? = null,
    val agentType: AgentType? = null,
)
