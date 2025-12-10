package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Base implementation of the [Agent] interface. Subclasses should override
 * [processRequest] or [processRequestFlow] to provide real behavior.
 *
 * @param _agentName The name of the agent.
 * @param _agentType The agent type string which will be mapped to [AgentType].
 */
abstract class BaseAgent(
    private val _agentName: String,
    private val _agentType: String,
) : Agent {

    override fun getName(): String? = _agentName

    override fun getType(): AgentType = try {
        AgentType.valueOf(_agentType.uppercase())
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Invalid agent type string: %s, defaulting to USER", _agentType)
        AgentType.USER
    }

    /**
     * Default synchronous handler for requests. Subclasses should override when performing real work.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.d("%s processing request: %s (context=%s)", _agentName, request.query, context)
        return AgentResponse(
            content = "BaseAgent response to '${request.query}' for agent $_agentName with context '$context'",
            confidence = 1.0f
        )
    }

    /**
     * Default streaming implementation that emits a single response produced by [processRequest].
     * Subclasses may override to provide incremental/streaming results.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, "DefaultContext_BaseAgentFlow"))
    }

    /** Returns simple capability metadata for this agent. */
    fun getCapabilities(): Map<String, Any> = mapOf(
        "name" to _agentName,
        "type" to _agentType,
        "base_implemented" to true
    )

    /** Placeholder for continuous memory (override to provide real memory). */
    fun getContinuousMemory(): Any? = null

    /** Default ethical guidelines for base agents. */
    fun getEthicalGuidelines(): List<String> = listOf(
        "Be helpful.",
        "Be harmless.",
        "Adhere to base agent principles."
    )

    /** Minimal learning history; override to return actual history. */
    open fun getLearningHistory(): List<String> = emptyList()

    /** Optional convenience hook used by some agents. */
    open fun iRequest(query: String, type: String, context: Map<String, String>) {
        // default no-op; override in implementations that require non-suspending adapters
        Timber.d("iRequest called on %s with query=%s type=%s", _agentName, query, type)
    }

    /** Optional initialization hook for adaptive protection/security subsystems. */
    open fun initializeAdaptiveProtection() {
        Timber.d("initializeAdaptiveProtection called for %s", _agentName)
    }
}
