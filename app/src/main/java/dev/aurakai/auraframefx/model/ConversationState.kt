package dev.aurakai.auraframefx.model

// TODO: Class reported as unused or needs implementation. Ensure this is utilized, e.g., by NeuralWhisper.
sealed class ConversationState {
    object Idle : ConversationState()
    object Listening : ConversationState()
    object Speaking : ConversationState()
    object Recording : ConversationState()
    data class Processing(val partialTranscript: String?) :
        ConversationState() // Added optional field

    data class Responding(val responseText: String?) :
        ConversationState() // Changed from Response for clarity

    data class Error(val errorMessage: String) : ConversationState()
    // Add other relevant states like Thinking, Interrupted, etc.
}
