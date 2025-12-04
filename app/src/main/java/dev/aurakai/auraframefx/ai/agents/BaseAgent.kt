package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.InteractionResponse
import dev.aurakai.auraframefx.models.agent_states.ActiveThreat
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.utils.toJsonObject
import java.io.Serializable

/**
 * Base Agent class for AI agents in the AuraFrameFX system.
 * Provides common functionality and lifecycle management for all agents.
 */
abstract class BaseAgent(agentName: String) {

    abstract val agentName: String
    abstract val agentType: String

    companion object {
        const val TOP_VL = "1.0.0"
    }

    /**
     * Validates input request
     */
    protected open fun validateRequest(request: AiRequest): Boolean {
        return request.prompt.isNotBlank()
    }

    /**
     * Pre-processes request before main processing
     */
    protected open suspend fun preprocessRequest(
        request: AiRequest,
        context: String
    ): Pair<AiRequest, String> {
        return Pair(request, context)
    }

    /**
     * Post-processes response after main processing
     */
    protected open suspend fun postprocessResponse(
        response: AgentResponse,
        request: AiRequest
    ): AgentResponse {
        return response
    }

    /**
     * Gets agent-specific configuration
     */
    protected open fun getAgentConfig(): Map<String, Any> {
        return mapOf(
            "name" to agentName,
            "type" to agentType,
            "version" to TOP_VL
        )
    }

    /**
     * Handles errors in a consistent way
     */
    protected fun handleError(error: Throwable, context: String = ""): AgentResponse {
        val errorMessage = when (error) {
            is IllegalArgumentException -> "Invalid input: ${error.message}"
            is SecurityException -> "Security error: Access denied"
            is java.net.ConnectException -> "Connection error: Unable to reach service"
            else -> "Unexpected error: ${error.message}"
        }

        return AgentResponse.error("$errorMessage${if (context.isNotEmpty()) " (Context: $context)" else ""}")
    }

    /**
     * Creates a success response with metadata
     */
    protected fun createSuccessResponse(
        content: String,
        metadata: Map<String, Any> = emptyMap()
    ): AgentResponse {
        return AgentResponse.success(
            content = content,
            agentName = agentName,
            metadata = metadata + getAgentConfig()
        )
    }

    /**
     * Creates a processing response
     */
    protected fun createProcessingResponse(message: String = "Processing..."): AgentResponse {
        return AgentResponse.processing("[$agentName] $message")
    }

    /**
     * Logs agent activity (can be overridden for specific logging needs)
     */
    protected open fun logActivity(activity: String, details: Map<String, Any> = emptyMap()) {
        println("[$agentName] $activity: $details")
    }

    /**
     * Factory method to create an InteractionResponse
     */
    protected open fun InteractionResponse(
        content: String,
        success: Boolean = true,
        timestamp: Long = System.currentTimeMillis(),
        metadata: Map<String, Any> = emptyMap()
    ): InteractionResponse {
        return InteractionResponse(
            content = content,
            success = success,
            metadata = metadata.toJsonObject(),
            timestamp = timestamp
        )
    }

    abstract fun iRequest(query: String, type: String, context: Map<String, String>)
    abstract fun iRequest()
    abstract fun initializeAdaptiveProtection()
    abstract fun addToScanHistory(scanEvent: Any)
    abstract fun analyzeSecurity(prompt: String): List<ActiveThreat>

    abstract val contextManager: ContextManager
}
