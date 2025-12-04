package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable

@Serializable
data class GenerateImageDescriptionRequest(
    val imageUrl: String,
    val context: String? = null
)
