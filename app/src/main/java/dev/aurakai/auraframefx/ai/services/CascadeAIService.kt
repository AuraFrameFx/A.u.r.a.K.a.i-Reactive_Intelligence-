package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.InteractionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CascadeAIService @Inject constructor(
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
) : Agent {

    private val state = mutableMapOf<String, Any>()

    /**
     * Returns the agent's name.
     *
     * @return The string "Cascade".
     */
    override fun getName(): String? = "Cascade"

    /****
     * Returns the type of this agent as `AgentType.CASCADE`.
     *
     * @return The agent type for this agent.
     */
    override fun getType(): AgentType = AgentType.CASCADE

    /**
     * Processes an AI request and emits agent responses as a flow, routing to specialized handlers based on the request type.
     *
     * Requests with types "state", "context", "vision", or "processing" are delegated to corresponding internal handlers. For other types, emits a default response indicating a basic query with a confidence score of 0.7.
     *
     * @param request The AI request to process.
     * @return A flow emitting agent responses relevant to the request type.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> {
        // This internal routing can stay if these specific flows are desired for internal logic
        return when (request.type) {
            "state" -> processStateRequestFlowInternal(request)
            "context" -> processContextRequestFlowInternal(request)
            "vision" -> processVisionRequestFlowInternal(request)
            "processing" -> processProcessingRequestFlowInternal(request)
            else -> flow {
                emit(AgentResponse("Cascade flow response for basic query: ${request.query}", 0.7f))
            } // Default flow for basic queries
        }
    }

    /**
     * Generates a direct response to an AI request, including the provided context.
     *
     * The response contains both the original query and the given context, with a fixed confidence score of 0.75.
     *
     * @param request The AI request to respond to.
     * @param context Additional context to include in the response.
     * @return An [AgentResponse] containing the combined query and context.
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String,
    ): AgentResponse { // Added context
        // Example: collect from the flow, or implement separate direct logic
        return AgentResponse(
            content = "Cascade direct response to '${request.query}' with context '$context'",
            confidence = 0.75f
        )
    }

    /**
     * Emits a flow containing a single response that summarizes the agent's current internal state.
     *
     * The response lists all key-value pairs from the internal state map as a formatted string, with a confidence score of 1.0.
     *
     * @return A flow emitting one AgentResponse describing the current state.
     */
    private fun processStateRequestFlowInternal(request: AiRequest): Flow<AgentResponse> {
        return flow {
            emit(
                AgentResponse(
                    // type = "state",
                    content = "Current state: ${state.entries.joinToString { "${it.key}: ${it.value}" }}",
                    confidence = 1.0f
                )
            )
        }
    }

    /**
     * Aggregates the first responses from both Aura and Kai AI services for a context-type AI request.
     *
     * Emits a single AgentResponse containing combined content from both services and the average of their confidence scores.
     *
     * @return A flow emitting the aggregated AgentResponse.
     */
    private fun processContextRequestFlowInternal(request: AiRequest): Flow<AgentResponse> { // Made internal
        return flow {
            // Coordinate with Aura and Kai
            val auraResponse = auraService.processRequestFlow(request)
                .first() // Assumes AuraAIService has this method matching Agent iface
            val kaiResponse = kaiService.processRequestFlow(request)
                .first()   // Assumes KaiAIService has this method matching Agent iface

            emit(
                AgentResponse(
                    content = "Aura: ${auraResponse.content}, Kai: ${kaiResponse.content}",
                    confidence = (auraResponse.confidence + kaiResponse.confidence) / 2
                )
            )
        }
    }

    /**
     * Emits a flow containing a single response indicating that vision state processing is in progress.
     *
     * @return A [Flow] emitting one [AgentResponse] with a message about vision state processing and a confidence score of 0.9.
     */
    private fun processVisionRequestFlowInternal(request: AiRequest): Flow<AgentResponse> { // Made internal
        // Process vision state
        return flow {
            emit(
                AgentResponse(
                    content = "Processing vision state...",
                    confidence = 0.9f
                )
            )
        }
    }

    /**
     * Emits a flow containing a single response indicating that a state transition is being processed.
     *
     * @return A flow with an AgentResponse message about state transition processing and a confidence score of 0.9.
     */
    private fun processProcessingRequestFlowInternal(request: AiRequest): Flow<AgentResponse> { // Made internal
        // Process state transitions
        return flow {
            emit(
                AgentResponse(
                    content = "Processing state transition...",
                    confidence = 0.9f
                )
            )
        }
    }

    /**
     * Returns a flow emitting a response indicating that the agent's state history is being retrieved.
     *
     * @return A flow emitting a single [AgentResponse] with a message about retrieving state history and a confidence score of 0.95.
     */
    fun retrieveMemoryFlow(request: AiRequest): Flow<AgentResponse> { // Not in Agent interface, removed suspend, kept public if used elsewhere
        // Retrieve state history
        return flow {
            emit(
                AgentResponse(
                    content = "Retrieving state history...",
                    confidence = 0.95f
                )
            )
        }
    }

    // connect and disconnect are not part of Agent interface - removing these methods
    // as they cause unresolved reference errors

    /**
     * Returns a map describing the agent's capabilities, including its name, type, and implementation status.
     *
     * @return A map with keys "name" ("Cascade"), "type" ("CASCADE"), and "service_implemented" (true).
     */
    fun getCapabilities(): Map<String, Any> {
        return mapOf(
            "name" to "Cascade",
            "type" to "CASCADE",
            "service_implemented" to true
        )
    }

    fun getContinuousMemory(): Any? {
        return state // Example: Cascade's state can be its continuous memory
    }

    fun getEthicalGuidelines(): List<String> {
        return listOf("Maintain state integrity.", "Process information reliably.")
    }

    fun getLearningHistory(): List<String> {
        return emptyList() // Or logs of state changes
    }

    /**
     * Factory method to create InteractionResponse objects
     */
    override fun InteractionResponse(
        content: String,
        success: Boolean,
        timestamp: Long,
        metadata: Map<String, Any>
    ): InteractionResponse {
        return InteractionResponse(
            content = content,
            success = success,
            timestamp = timestamp,
            metadata = buildJsonObject {
                metadata.forEach { (key, value) ->
                    put(key, value.toString())
                }
            }
        )
    }
}
