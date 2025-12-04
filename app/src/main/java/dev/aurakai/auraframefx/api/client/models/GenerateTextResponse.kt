package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextResponse(
    val text: String,
    val tokensUsed: Int? = null
)
