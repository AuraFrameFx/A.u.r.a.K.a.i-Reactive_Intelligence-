/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val maxTokens: Int = 500,
    val temperature: Float = 0.7f
)

