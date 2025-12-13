package dev.aurakai.auraframefx.ai.context

// TODO: Class reported as unused or needs implementation. Ensure this is utilized by ContextManager.
data class AIContext(
    val currentPrompt: String? = null, // TODO: Needs implementation detail if used
    val history: List<String> = emptyList(), // TODO: Needs implementation detail if used
    // Add other relevant contextual information, e.g.,
    // val sessionId: String?,
    // val userProfile: UserProfile?,
    // val deviceState: DeviceState?
)
