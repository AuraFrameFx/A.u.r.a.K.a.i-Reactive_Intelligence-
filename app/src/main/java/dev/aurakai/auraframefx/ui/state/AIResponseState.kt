package dev.aurakai.auraframefx.ui.state

/**
 * Represents the state of an AI text response operation.
 */
sealed class AIResponseState {
    object Idle : AIResponseState()
    object Loading : AIResponseState()

    /**
     * Represents a successful text response.
     * @param _text The response text. Parameter reported as unused.
     */
    data class Success(val _text: String) :
        AIResponseState() // TODO: Param _text reported as unused.

    /**
     * Represents an error during text response generation.
     * @param _message The error message. Parameter reported as unused.
     */
    data class Error(val _message: String) :
        AIResponseState() // TODO: Param _message reported as unused.
}
