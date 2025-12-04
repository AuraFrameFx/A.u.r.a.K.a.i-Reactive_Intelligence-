package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val maxTokens: Int = 500,
    val temperature: Float = 0.7f
)
