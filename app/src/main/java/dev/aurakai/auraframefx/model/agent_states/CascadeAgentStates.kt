package dev.aurakai.auraframefx.models.agent_states

// TODO: Define actual properties for these states.
// TODO: Classes reported as unused or need implementation. Ensure these are utilized by CascadeAgent.

data class VisionState(
    val lastObservation: String? = null,
    val objectsDetected: List<String> = emptyList(),
    // Add other relevant vision state properties
)

data class ProcessingState(
    val currentStep: String? = null,
    val progressPercentage: Float = 0.0f,
    val isError: Boolean = false,
    // Add other relevant processing state properties
)
