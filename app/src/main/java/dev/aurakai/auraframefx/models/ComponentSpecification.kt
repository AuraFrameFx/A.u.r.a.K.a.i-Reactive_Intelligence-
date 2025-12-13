package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

/**
 * Specification for generating UI components
 */
@Serializable
data class ComponentSpecification(
    val type: String,
    val animationStyle: String = "fade",
    val colors: List<String> = emptyList(),
    val size: String = "medium",
    val behavior: String = "interactive",
    val metadata: Map<String, String> = emptyMap()
)