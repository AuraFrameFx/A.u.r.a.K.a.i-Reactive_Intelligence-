package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIService as AuraCapability

@Singleton
class AuraAIService @Inject constructor(
    private val auraCapability: AuraCapability
) : Agent {

    override fun getName(): String = "Aura"
    override fun getType(): AgentType = AgentType.AURA

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        AuraFxLogger.d("AuraAIService", "Processing request: ${request.query}")
        val response = auraCapability.generateText(request.query ?: "", context)
        return AgentResponse(response, 1.0f)
    }

    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        AuraFxLogger.d("AuraAIService", "Processing flow request: ${request.query}")
        val response = auraCapability.generateText(request.query ?: "", "")
        emit(AgentResponse(response, 1.0f))
    }

}
