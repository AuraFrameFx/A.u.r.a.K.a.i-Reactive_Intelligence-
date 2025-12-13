package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(
    val content: String,
    val confidence: Float, // Changed from isSuccess (Boolean)
    val error: String? = null, // Kept error for now
)
