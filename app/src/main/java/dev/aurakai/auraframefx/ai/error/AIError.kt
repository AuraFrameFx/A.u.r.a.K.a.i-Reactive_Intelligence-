package dev.aurakai.auraframefx.ai.error

import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.serialization.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AIError(
    val id: String = UUID.randomUUID().toString(),
    val agent: AgentType,
    val type: ErrorType,
    val message: String,
    val context: String,
    val metadata: Map<String, String> = emptyMap(),
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant = Clock.System.now(),
    val recoveryActions: List<RecoveryAction> = emptyList()
)

@Serializable
enum class ErrorType {
    PROCESSING_ERROR,
    MEMORY_ERROR,
    CONTEXT_ERROR,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    INTERNAL_ERROR
}

@Serializable
data class RecoveryAction(
    val actionType: RecoveryActionType,
    val description: String
)

@Serializable
enum class RecoveryActionType {
    RETRY,
    FALLBACK,
    RESTART,
    RECONFIGURE,
    NOTIFY,
    ESCALATE
}

enum class RecoveryResult {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILURE
}
