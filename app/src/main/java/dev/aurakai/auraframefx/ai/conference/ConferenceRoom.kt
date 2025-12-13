package dev.aurakai.auraframefx.ai.conference

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent

class ConferenceRoom(
    val name: String,
    val orchestrator: GenesisAgent,
) {
    private val agents = mutableSetOf<Agent>()
    private val history = mutableListOf<String>()
    private var context: Map<String, Any> = emptyMap()

    // --- Advanced Features ---
    var createdAt: Long = System.currentTimeMillis()
        private set
    var lastActivityAt: Long = createdAt
        private set
    private val asyncTaskQueue = mutableListOf<Pair<String, suspend () -> Any>>()
    private val webhookCallbacks = mutableListOf<(String, Any) -> Unit>()
    private var requestCount: Int = 0
    private val errorLog = mutableListOf<String>()

    fun join(agent: Agent) {
        agents.add(agent)
    }

    fun leave(agent: Agent) {
        agents.remove(agent)
    }

    fun broadcastContext(newContext: Map<String, Any>) {
        context = newContext
        // Optionally, notify all agents
    }

    fun addToHistory(entry: String) {
        history.add(entry)
    }

    fun getHistory(): List<String> = history
    fun getAgents(): Set<Agent> = agents
    fun getContext(): Map<String, Any> = context

    suspend fun orchestrateConversation(userInput: String): List<Any> {
        // Use GenesisAgent to orchestrate a multi-agent conversation
        val agentList = agents.toList()
        val responses = orchestrator.participateWithAgents(context, agentList, userInput)
        // Add to history
        responses.values.forEach { addToHistory(it.toString()) }
        return responses.values.toList()
    }

    /**
     * Aggregates responses from all agents for consensus or decision-making.
     * Returns the consensus response or a map of agent responses.
     */
    fun aggregateConsensus(responses: List<Map<String, dev.aurakai.auraframefx.models.AgentResponse>>): Map<String, dev.aurakai.auraframefx.models.AgentResponse> {
        return orchestrator.aggregateAgentResponses(responses)
    }

    /**
     * Distributes the current context/memory to all agents for shared state.
     */
    fun distributeContext() {
        orchestrator.shareContextWithAgents()
    }

    /**
     * Returns a snapshot of the conference room state for diagnostics or UI.
     */
    fun getRoomSnapshot(): Map<String, Any> = mapOf(
        "name" to name,
        "agents" to agents.map { it.getName() },
        "context" to context,
        "history" to history
    )

    fun persistHistory(persist: (List<String>) -> Unit) {
        persist(history)
    }

    fun loadHistory(load: () -> List<String>) {
        history.clear()
        history.addAll(load())
    }

    fun registerWebhook(callback: (event: String, payload: Any) -> Unit) {
        webhookCallbacks.add(callback)
    }

    fun logError(error: String) {
        errorLog.add("[${System.currentTimeMillis()}] $error")
    }

    fun getErrorLog(): List<String> = errorLog

    fun incrementRequestCount() {
        requestCount++
        lastActivityAt = System.currentTimeMillis()
    }

    fun getRequestCount(): Int = requestCount

    fun queueAsyncTask(taskId: String, task: suspend () -> Any) {
        asyncTaskQueue.add(taskId to task)
    }

    suspend fun processNextAsyncTask(): Any? {
        val next = asyncTaskQueue.firstOrNull() ?: return null
        asyncTaskQueue.removeAt(0)
        return try {
            val result = next.second()
            webhookCallbacks.forEach { it("task_completed", result) }
            result
        } catch (e: Exception) {
            logError("Async task failed: ${e.message}")
            webhookCallbacks.forEach { it("task_failed", e.message ?: "Unknown error") }
            null
        }
    }

    // --- Utility & Diagnostics ---
    fun getRoomMetadata(): Map<String, Any> = mapOf(
        "name" to name,
        "createdAt" to createdAt,
        "lastActivityAt" to lastActivityAt,
        "agentCount" to agents.size,
        "requestCount" to requestCount,
        "asyncQueueSize" to asyncTaskQueue.size,
        "errorCount" to errorLog.size
    )

    fun clearErrorLog() {
        errorLog.clear()
    }

    fun clearHistory() {
        history.clear()
    }

    fun clearAsyncQueue() {
        asyncTaskQueue.clear()
    }

    // --- Extensibility: Custom Room Properties ---
    private val customProperties = mutableMapOf<String, Any>()
    fun setCustomProperty(key: String, value: Any) {
        customProperties[key] = value
    }

    fun getCustomProperty(key: String): Any? = customProperties[key]
    fun getAllCustomProperties(): Map<String, Any> = customProperties.toMap()
}
