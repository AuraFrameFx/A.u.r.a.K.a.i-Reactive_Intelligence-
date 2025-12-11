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

    /**
 * Retrieve the agent's configured name.
 *
 * @return The agent's name, or null if not configured.
 */
override fun getName(): String? = _agentName

    /**
     * Resolves the agent's configured type string to an AgentType enum, falling back to USER when the value is unrecognized.
     *
     * Logs a warning if the configured type cannot be parsed as an AgentType.
     *
     * @return The resolved AgentType; `AgentType.USER` if the configured `_agentType` is invalid.
     */
    override fun getType(): AgentType = try {
        AgentType.valueOf(_agentType.uppercase())
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "Invalid agent type string: %s, defaulting to USER", _agentType)
        AgentType.USER
    }

    /**
     * Handle an incoming AI request synchronously and produce a default AgentResponse.
     *
     * Subclasses override to provide real processing logic.
     *
     * @param request The incoming AiRequest to handle.
     * @param context Contextual information (e.g., conversation or environment) used when producing the response.
     * @return An AgentResponse containing a default content string referencing the request and agent, with a confidence of 1.0f.
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

    /**
     * Provide basic capability metadata for this agent.
     *
     * @return A map with keys:
     *  - `name`: the agent's configured name,
     *  - `type`: the agent's configured type string,
     *  - `base_implemented`: `true` indicating the base agent implementation is present.
     */
    fun getCapabilities(): Map<String, Any> = mapOf(
        "name" to _agentName,
        "type" to _agentType,
        "base_implemented" to true
    )

    /**
 * Provides the agent's continuous memory storage; override to return a concrete memory object.
 *
 * @return The continuous memory object used by the agent, or `null` if the agent has no continuous memory.
 */
    fun getContinuousMemory(): Any? = null

    /**
     * Default ethical guidelines for the base agent.
     *
     * @return A list containing three guideline strings: "Be helpful.", "Be harmless.", and "Adhere to base agent principles."
     */
    fun getEthicalGuidelines(): List<String> = listOf(
        "Be helpful.",
        "Be harmless.",
        "Adhere to base agent principles."
    )

    /**
 * Provides the agent's recorded learning history.
 *
 * @return A list of learning-history entries; empty by default. Override to supply real history.
 */
    open fun getLearningHistory(): List<String> = emptyList()

    /**
     * Optional non-suspending adapter hook for submitting a query to the agent.
     *
     * Default implementation performs no action; override to handle immediate (non-suspending) requests
     * or to provide lightweight adapters that don't require coroutine support.
     *
     * @param query The query or message to process.
     * @param type A short string describing the request type or intent.
     * @param context Additional contextual values as key/value pairs.
     */
    open fun iRequest(query: String, type: String, context: Map<String, String>) {
        // default no-op; override in implementations that require non-suspending adapters
        Timber.d("iRequest called on %s with query=%s type=%s", _agentName, query, type)
    }

    /** Optional initialization hook for adaptive protection/security subsystems. */
    open fun initializeAdaptiveProtection() {
        Timber.d("initializeAdaptiveProtection called for %s", _agentName)
    }
}