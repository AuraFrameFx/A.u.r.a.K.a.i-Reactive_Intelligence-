package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

/**
 * Represents the mode of conversation flow
 */
@Serializable
enum class ConversationMode {
    TURN_ORDER,
    FREE_FORM
}

/**
 * Represents enhanced interaction data with additional context
 */
@Serializable
data class EnhancedInteractionData(
    val content: String,
    val type: InteractionType,
    val timestamp: String,
    val context: Map<String, String> = emptyMap(),
    val enrichmentData: Map<String, String> = emptyMap(),
    val emotion: String? = null,
)

@Serializable
data class InteractionData(
    val content: String,
    val type: String = "text",
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Response to a user interaction
 */
@Serializable
data class InteractionResponse(
    val content: String,
    val agent: String,
    val confidence: Float,
    val timestamp: String,
    val metadata: Map<String, String> = emptyMap(),
)

enum class CreativeIntent {
    ARTISTIC,
    FUNCTIONAL,
    EXPERIMENTAL,
    EMOTIONAL
}

/**
 * Represents interaction types
 */
enum class InteractionType {
    TEXT,
    VOICE,
    IMAGE,
    VIDEO,
    GESTURE,
    SYSTEM
}

/**
 * Represents agent request for processing
 */
@Serializable
data class AgentRequest(
    val query: String,
    val type: String = "text",
    val context: Map<String, String> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
)

/**
 * Represents AI request for processing
 */
@Serializable
data class AiRequest(
    val query: String,
    val type: String = "text",
    val context: Map<String, String> = emptyMap(),
)

/**
 * Represents text generation request
 */
@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val maxTokens: Int = 1000,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
)

/**
 * Represents text generation response
 */
@Serializable
data class GenerateTextResponse(
    val generatedText: String,
    val finishReason: String = "completed",
    val usage: Map<String, Int> = emptyMap(),
)
