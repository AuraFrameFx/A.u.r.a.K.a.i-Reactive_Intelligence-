package dev.aurakai.auraframefx.ai.context

import kotlinx.serialization.Serializable

/**
 * Represents a memory item with context awareness
 */
@Serializable
data class ContextMemory(
    val content: String,
    val relevanceScore: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val context: Map<String, Any> = emptyMap(),
    val metadata: Map<String, String> = emptyMap()
)