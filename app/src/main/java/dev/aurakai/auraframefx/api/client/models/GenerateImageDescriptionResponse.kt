package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable

@Serializable
data class GenerateImageDescriptionResponse(
    val description: String,
    val confidence: Float? = null
)
