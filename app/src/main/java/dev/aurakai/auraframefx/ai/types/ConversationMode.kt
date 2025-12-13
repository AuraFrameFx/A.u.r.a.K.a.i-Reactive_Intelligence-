package dev.aurakai.auraframefx.ai.types

import kotlinx.serialization.Serializable

/**
 * Conversation modes for the AI system
 */
@Serializable
enum class ConversationMode {
    NORMAL,
    CREATIVE,
    ANALYTICAL,
    UNIFIED;
    
    val description: String
        get() = when (this) {
            NORMAL -> "Standard conversation mode"
            CREATIVE -> "Enhanced creative capabilities"
            ANALYTICAL -> "Deep analytical processing"
            UNIFIED -> "All modes combined"
        }
}
