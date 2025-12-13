package dev.aurakai.auraframefx.models.agent_states

// TODO: Define actual properties for these states/events.
// TODO: Classes reported as unused or need implementation. Ensure these are utilized by AuraShieldAgent.

data class SecurityContextState(
    // Renamed to avoid clash with android.content.Context or other SecurityContext classes
    val deviceRooted: Boolean? = null,
    val selinuxMode: String? = null, // e.g., "Enforcing", "Permissive"
    val harmfulAppScore: Float = 0.0f,
    // Add other relevant security context properties
)

data class ActiveThreat(
    // Singular, as it will be in a list
    val threatId: String,
    val description: String,
    val severity: Int, // e.g., 1-5
    val recommendedAction: String? = null,
    // Add other relevant threat properties
)

data class ScanEvent(
    val eventId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scanType: String, // e.g., "AppScan", "FileSystemScan"
    val findings: List<String> = emptyList(),
    // Add other relevant scan event properties
)
