package dev.aurakai.auraframefx.api.client.models

import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfigNotchBar(
    val enabled: Boolean = true,
    val height: Int = 0,
    val color: String? = null
)
