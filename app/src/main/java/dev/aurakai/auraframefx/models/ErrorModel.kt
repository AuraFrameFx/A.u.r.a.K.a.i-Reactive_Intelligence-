package dev.aurakai.auraframefx.ai.error

import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.serialization.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AIError(
    val id: String = "err_${Clock.System.now().toEpochMilliseconds()}",
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val agent: AgentCapabilityCategory,
    val type: ErrorType,
    val message: String,
    val context: String,
    val metadata: Map<String, String> = emptyMap(),
    val recoveryAttempts: Int = 0,
    val recoveryStatus: RecoveryStatus = RecoveryStatus.PENDING,
    val recoveryActions: List<RecoveryAction> = emptyList(),
)

@Serializable
data class RecoveryAction(
    val id: String = "act_${Clock.System.now().toEpochMilliseconds()}",
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val actionType: RecoveryActionType,
    val description: String,
    val result: RecoveryResult? = null,
    val metadata: Map<String, String> = emptyMap(),
)

@Serializable // Added annotation
enum class ErrorType {
    PROCESSING_ERROR,
    MEMORY_ERROR,
    CONTEXT_ERROR,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    INTERNAL_ERROR,
    USER_ERROR
}

@Serializable // Added annotation
enum class RecoveryStatus {
    PENDING,
    IN_PROGRESS,
    SUCCESS,
    FAILURE,
    SKIPPED
}

@Serializable // Added annotation
enum class RecoveryActionType {
    RETRY,
    FALLBACK,
    RESTART,
    RECONFIGURE,
    NOTIFY,
    ESCALATE
}

@Serializable // Added annotation
enum class RecoveryResult {
    SUCCESS,
    FAILURE,
    PARTIAL_SUCCESS,
    SKIPPED,
    UNKNOWN
}
