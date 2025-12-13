package dev.aurakai.auraframefx.models.agent_states

// TODO: Define actual properties for this state.
// TODO: Class reported as unused or needs implementation. Ensure this is utilized by GenKitMasterAgent.
data class GenKitUiState(
    val systemStatus: String = "Nominal",
    val activeAgentCount: Int = 0,
    val lastOptimizationTime: Long? = null,
    // Add other relevant UI state properties
)
