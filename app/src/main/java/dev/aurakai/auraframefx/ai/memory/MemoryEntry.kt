package dev.aurakai.auraframefx.ai.memory

import kotlinx.serialization.Serializable

/**
 * Represents a memory entry stored in the DefaultMemoryManager
 */
@Serializable
data class MemoryEntry(
    val key: String? = null,
    val value: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relevanceScore: Float = 0.0f,
    val metadata: Map<String, String> = emptyMap()
)