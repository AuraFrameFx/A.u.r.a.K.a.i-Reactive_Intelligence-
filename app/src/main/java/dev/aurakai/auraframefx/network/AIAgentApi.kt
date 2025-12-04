package dev.aurakai.auraframefx.network

import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.AgentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API interface for interacting with AI agents in the Trinity system.
 */
interface AIAgentApi {
    /**
     * Get the status of a specific AI agent.
     *
     * @param agentType The type of the agent (e.g., "genesis", "aura", "kai").
     * @return The agent's status and information.
     */
    @GET("agent/{agentType}/status")
    suspend fun getAgentStatus(
        @Path("agentType") agentType: String,
    ): AgentResponse

    /**
     * Send a request to an AI agent for processing.
     *
     * @param agentType The type of the agent to process the request.
     * @param request The request data to be processed.
     * @return The agent's response to the request.
     */
    @POST("agent/{agentType}/process-request")
    suspend fun processRequest(
        @Path("agentType") agentType: String,
        @Body request: AgentRequest,
    ): AgentResponse

    // Add more agent-related endpoints as needed
}
