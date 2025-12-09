package dev.aurakai.auraframefx.ai.error

import android.util.Log
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.serialization.InstantSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

interface ErrorHandler {
    fun handle(t: Throwable)
}

class DefaultErrorHandler : ErrorHandler {
    override fun handle(t: Throwable) {
        Log.w("DefaultErrorHandler", "Caught error", t)
    }
}

@Singleton
class ErrorHandlerImpl @Inject constructor(
    private val contextManager: ContextManager,
    private val config: AIPipelineConfig,
) : ErrorHandler {
    private val _errors = MutableStateFlow(mapOf<String, AIError>())
    val errors: StateFlow<Map<String, AIError>> = _errors

    private val _errorStats = MutableStateFlow(ErrorStats())
    val errorStats: StateFlow<ErrorStats> = _errorStats

    /**
     * Processes an error by determining its type, recording its details, updating error statistics, and triggering appropriate recovery actions.
     *
     * All metadata values are converted to strings before being stored in the error record.
     *
     * @param error The exception or error to process.
     * @param agent The agent associated with the error.
     * @param context A description of where or how the error occurred.
     * @param metadata Additional metadata about the error; all values are stringified.
     * @return The created and recorded AIError instance.
     */
    fun handleError(
        error: Throwable,
        agent: AgentType,
        context: String,
        metadata: Map<String, Any> = emptyMap(),
    ): AIError {
        val errorType = determineErrorType(error)
        val errorMessage = error.message ?: "Unknown error"

        val aiError = AIError(
            agent = agent,
            type = errorType,
            message = errorMessage,
            context = context,
            metadata = metadata.mapValues { it.value.toString() }
        )

        _errors.update { current ->
            current + (aiError.id to aiError)
        }

        updateStats(aiError)
        attemptRecovery(aiError)
        return aiError
    }

    private fun determineErrorType(error: Throwable): ErrorType {
        return when (error) {
            is ProcessingException -> ErrorType.PROCESSING_ERROR
            is MemoryException -> ErrorType.MEMORY_ERROR
            is ContextException -> ErrorType.CONTEXT_ERROR
            is NetworkException -> ErrorType.NETWORK_ERROR
            is TimeoutException -> ErrorType.TIMEOUT_ERROR
            else -> ErrorType.INTERNAL_ERROR
        }
    }

    private fun attemptRecovery(error: AIError) {
        getRecoveryActions(error)

        error.recoveryActions.forEach { action ->
            when (action.actionType) {
                RecoveryActionType.RETRY -> attemptRetry(error)
                RecoveryActionType.FALLBACK -> attemptFallback(error)
                RecoveryActionType.RESTART -> attemptRestart(error)
                RecoveryActionType.RECONFIGURE -> attemptReconfigure(error)
                RecoveryActionType.NOTIFY -> notifyError(error)
                RecoveryActionType.ESCALATE -> escalateError(error)
            }
        }
    }

    private fun getRecoveryActions(error: AIError): List<RecoveryAction> {
        return when (error.type) {
            ErrorType.PROCESSING_ERROR -> listOf(
                RecoveryAction(
                    actionType = RecoveryActionType.RETRY,
                    description = "Retrying processing with modified parameters"
                ),
                RecoveryAction(
                    actionType = RecoveryActionType.FALLBACK,
                    description = "Falling back to simpler processing method"
                )
            )

            ErrorType.MEMORY_ERROR -> listOf(
                RecoveryAction(
                    actionType = RecoveryActionType.RECONFIGURE,
                    description = "Reconfiguring memory settings"
                ),
                RecoveryAction(
                    actionType = RecoveryActionType.RESTART,
                    description = "Restarting memory system"
                )
            )

            ErrorType.CONTEXT_ERROR -> listOf(
                RecoveryAction(
                    actionType = RecoveryActionType.RETRY,
                    description = "Retrying with updated context"
                ),
                RecoveryAction(
                    actionType = RecoveryActionType.RECONFIGURE,
                    description = "Reconfiguring context parameters"
                )
            )

            else -> listOf(
                RecoveryAction(
                    actionType = RecoveryActionType.NOTIFY,
                    description = "Notifying system of error"
                )
            )
        }
    }

    private fun attemptRetry(error: AIError): RecoveryResult {
        // Implementation of retry logic
        return RecoveryResult.PARTIAL_SUCCESS
    }

    private fun attemptFallback(error: AIError): RecoveryResult {
        // Implementation of fallback logic
        return RecoveryResult.PARTIAL_SUCCESS
    }

    private fun attemptRestart(error: AIError): RecoveryResult {
        // Implementation of restart logic
        return RecoveryResult.PARTIAL_SUCCESS
    }

    private fun attemptReconfigure(error: AIError): RecoveryResult {
        // Implementation of reconfiguration logic
        return RecoveryResult.PARTIAL_SUCCESS
    }

    private fun notifyError(error: AIError): RecoveryResult {
        // Implementation of notification logic
        return RecoveryResult.SUCCESS
    }

    private fun escalateError(error: AIError): RecoveryResult {
        // Implementation of escalation logic
        return RecoveryResult.PARTIAL_SUCCESS
    }

    private fun updateStats(error: AIError) {
        _errorStats.update { current ->
            current.copy(
                totalErrors = current.totalErrors + 1,
                activeErrors = current.activeErrors + 1,
                lastError = error,
                errorTypes = current.errorTypes + (error.type to (current.errorTypes[error.type]
                    ?: 0) + 1),
                lastUpdated = Clock.System.now()
            )
        }
    }
}

@Serializable // Added Serializable
data class ErrorStats(
    val totalErrors: Int = 0,
    val activeErrors: Int = 0,
    val lastError: AIError? = null, // AIError would also need to be @Serializable if ErrorStats is
    val errorTypes: Map<ErrorType, Int> = emptyMap(), // ErrorType would also need to be @Serializable
    @Serializable(with = InstantSerializer::class) val lastUpdated: Instant = Clock.System.now(),
)

// Assuming AIError and ErrorType will be made serializable if ErrorStats is used in a serializable context.
// For now, only marking ErrorStats and its Instant field.

class ProcessingException(message: String? = null) : Exception(message)

class MemoryException(message: String? = null) : Exception(message)

class ContextException(message: String? = null) : Exception(message)

class NetworkException(message: String? = null) : Exception(message)

class TimeoutException(message: String? = null) : Exception(message)
