package dev.aurakai.auraframefx.system.overlay.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemOverlayConfig(
    val notchBar: NotchBarConfig? = null,
)

@Serializable
data class NotchBarConfig(
    val enabled: Boolean = false,
    val style: String = "default",
)
