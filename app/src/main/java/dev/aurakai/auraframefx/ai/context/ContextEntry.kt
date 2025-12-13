package dev.aurakai.auraframefx.ai.context

import kotlinx.serialization.Serializable

/**
 * Represents a context operation entry in the context history
 */
@Serializable
data class ContextEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val operation: String,
    val data: Map<String, Any> = emptyMap(),
    val metadata: Map<String, String> = emptyMap()
)