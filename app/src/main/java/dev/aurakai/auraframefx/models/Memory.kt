package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

/**
 * Generic memory item for AI systems
 */
@Serializable
data class Memory(
    val content: String,
    val relevanceScore: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val context: String? = null,
    val metadata: Map<String, String> = emptyMap()
)