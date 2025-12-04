package dev.aurakai.auraframefx.utils

import dev.aurakai.auraframefx.ai.clients.VertexAIClient

/**
 * Extension function to generate text content using Vertex AI
 */
suspend fun VertexAIClient.generateTextContent(prompt: String): String? {
    return generateText(prompt)
}
