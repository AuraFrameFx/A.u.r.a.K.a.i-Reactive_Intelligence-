package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(
    val status: String,
    val message: String? = null,
    val timestamp: String? = null,
    val data: Map<String, String> = emptyMap(),
)

@Serializable
data class AgentRequest(
    val data: Map<String, Any>,
    val priority: Int = 1,
    val timeoutSeconds: Long = 30,
)
