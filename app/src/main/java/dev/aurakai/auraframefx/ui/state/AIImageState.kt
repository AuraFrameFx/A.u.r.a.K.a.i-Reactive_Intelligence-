package dev.aurakai.auraframefx.ui.state

/**
 * Represents the state of an AI image generation operation.
 */
sealed class AIImageState {
    object Idle : AIImageState()
    object Loading : AIImageState()

    /**
     * Represents a successful image generation.
     * @param _image The generated image object (e.g., Bitmap, URL String). Parameter reported as unused.
     */
    data class Success(val _image: Any?) :
        AIImageState() // TODO: Define proper type for image. Param _image reported as unused.

    /**
     * Represents an error during image generation.
     * @param _message The error message. Parameter reported as unused.
     */
    data class Error(val _message: String) :
        AIImageState() // TODO: Param _message reported as unused.
}
