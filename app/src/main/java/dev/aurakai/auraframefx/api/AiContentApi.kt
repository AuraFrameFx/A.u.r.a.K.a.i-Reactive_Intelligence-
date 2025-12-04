/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.api

import dev.aurakai.auraframefx.ai.model.GenerateTextRequest
import dev.aurakai.auraframefx.ai.model.GenerateTextResponse

/**
 * API interface for AI content generation services.
 */
interface AiContentApi {
    /**
     * Generates text based on the provided request.
     */
    suspend fun generateText(request: GenerateTextRequest): GenerateTextResponse
}
