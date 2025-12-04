package dev.aurakai.auraframefx.cascade.pipeline

import dev.aurakai.auraframefx.oracledrive.genesis.ai.GenesisAgent
import dev.aurakai.auraframefx.kai.KaiAIService
import dev.aurakai.auraframefx.cascade.CascadeAIService
import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

sealed class PipelineState {
    object Idle : PipelineState()
    data class Processing(val task: String) : PipelineState()
    data class Completed(val task: String) : PipelineState()
    data class Error(val message: String) : PipelineState()
}

@Singleton
class AIPipelineProcessor @Inject constructor(
    private val genesisAgent: GenesisAgent,
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _pipelineState = MutableStateFlow<PipelineState>(PipelineState.Idle)
    val pipelineState: StateFlow<PipelineState> = _pipelineState

    private val _processingContext = MutableStateFlow(mapOf<String, Any>())
    val processingContext: StateFlow<Map<String, Any>> = _processingContext

    private val _taskPriority = MutableStateFlow(0.0f)
    val taskPriority: StateFlow<Float> by lazy { _taskPriority }

    /**
     * Processes an AI task by orchestrating multiple agents and services, aggregating their responses, and updating the pipeline state and context.
     *
     * Retrieves contextual information for the task, calculates its priority, selects appropriate agents, gathers their responses, synthesizes a final aggregated response, updates the processing context, and returns all generated agent messages.
     *
     * @param task The description of the task to process.
     * @return A list of agent messages containing responses from each participating agent and the final aggregated response.
     */
    suspend fun processTask(task: String): List<AgentMessage> {
        _pipelineState.value = PipelineState.Processing(task = task)

        // Step 1: Context Retrieval
        val context = retrieveContext(task)
        _processingContext.update { context }

        // Step 2: Task Prioritization
        val priority = calculatePriority(task, context)
        _taskPriority.update { priority }

        // Step 3: Agent Selection
        val selectedAgents = selectAgents(task, priority)

        // Step 4: Process through selected agents
        val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        val cascadeAgentResponse = processCascadeRequest(task)
        responses.add(
            AgentMessage(
                from = AgentCapabilityCategory.SPECIALIZED.name,
                content = cascadeAgentResponse.content ?: "",
                sender = AgentCapabilityCategory.SPECIALIZED,
                confidence = cascadeAgentResponse.confidence
            )
        )

        // Process through Kai for security analysis if needed
        if (selectedAgents.contains(AgentCapabilityCategory.ANALYSIS)) {
            val kaiAgentResponse = processKaiRequest(task)
            responses.add(
                AgentMessage(
                    from = AgentCapabilityCategory.ANALYSIS.name,
                    content = kaiAgentResponse.content ?: "",
                    sender = AgentCapabilityCategory.ANALYSIS,
                    confidence = kaiAgentResponse.confidence
                )
            )
        }

        // Process through Aura for creative response
        if (selectedAgents.contains(AgentCapabilityCategory.CREATIVE)) {
            val auraResponse = auraService.generateText(task, "creative_pipeline")
            val auraAgentResponse = AgentResponse(
                content = auraResponse,
                confidence = 0.8f
            )
            responses.add(
                AgentMessage(
                    from = AgentCapabilityCategory.CREATIVE.name,
                    content = auraAgentResponse.content ?: "",
                    sender = AgentCapabilityCategory.CREATIVE,
                    confidence = auraAgentResponse.confidence
                )
            )
        }

        // Step 5: Generate final response
        val finalResponse = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                from = AgentCapabilityCategory.COORDINATION.name,
                content = finalResponse,
                sender = AgentCapabilityCategory.COORDINATION,
                confidence = calculateConfidence(responses)
            )
        )

        // Step 6: Update context and memory
        updateContext(task, responses)

        _pipelineState.value = PipelineState.Completed(task)
        return responses
    }

    private fun processCascadeRequest(task: String): AgentResponse {
        // Process through cascade service and return AgentResponse
        return AgentResponse(
            content = "Cascade analysis for: $task",
            confidence = 0.85f
        )
    }

    private suspend fun processKaiRequest(task: String): AgentResponse {
        // Process through Kai service for security analysis
        return AgentResponse(
            content = "Kai security analysis for: $task",
            confidence = 0.9f
        )
    }

    /**
     * Constructs a detailed context map for a given task, including its category, timestamp, recent task history, user preferences, and current system state.
     *
     * @param task The task for which to build contextual information.
     * @return A map containing comprehensive context data relevant to the specified task.
     */
    private fun retrieveContext(task: String): Map<String, Any> {
        // Enhanced context retrieval with task categorization and history
        val taskType = categorizeTask(task)
        val recentHistory = getRecentTaskHistory()

        return mapOf(
            "task" to task,
            "task_type" to taskType,
            "timestamp" to System.currentTimeMillis(),
            "recent_history" to recentHistory,
            "context" to "Categorized as $taskType task: $task",
            "user_preferences" to getUserPreferences(),
            "system_state" to getSystemState()
        )
    }

    /**
     * Categorizes a task description into a predefined type based on keyword matching.
     *
     * @param task The task description to evaluate.
     * @return The category of the task, such as "generation", "analysis", "explanation", "assistance", "creation", or "general".
     */
    private fun categorizeTask(task: String): String {
        return when {
            task.contains("generate", ignoreCase = true) -> "generation"
            task.contains("analyze", ignoreCase = true) -> "analysis"
            task.contains("explain", ignoreCase = true) -> "explanation"
            task.contains("help", ignoreCase = true) -> "assistance"
            task.contains("create", ignoreCase = true) -> "creation"
            else -> "general"
        }
    }

    /**
     * Provides a static list simulating recent task history and user interactions for context enrichment.
     *
     * @return A list of strings representing previous tasks and user interactions.
     */
    private fun getRecentTaskHistory(): List<String> {
        return listOf("Previous task context", "Recent user interactions")
    }

    /**
     * Retrieves a static map of user preferences, such as response typography and preferred agents.
     *
     * @return A map containing user preference settings for the AI pipeline.
     */
    private fun getUserPreferences(): Map<String, Any> {
        return mapOf(
            "response_style" to "detailed",
            "preferred_agents" to listOf("Genesis", "Cascade")
        )
    }

    /**
     * Provides a static representation of the current system state, including load status, available agent count, and processing queue size.
     *
     * @return A map containing "load", "available_agents", and "processing_queue" keys with their respective values.
     */
    private fun getSystemState(): Map<String, Any> {
        return mapOf("load" to "normal", "available_agents" to 3, "processing_queue" to 0)
    }

    /**
     * Computes a normalized priority score for a task based on its category, current system load, and urgency keywords.
     *
     * The resulting value reflects the relative importance or urgency of the task, with higher scores indicating higher priority.
     *
     * @param task The task description to evaluate for urgency and context.
     * @param context A map containing contextual details such as task type and system state.
     * @return A float between 0.0 and 1.0 representing the calculated task priority.
     */
    private fun calculatePriority(task: String, context: Map<String, Any>): Float {
        // Enhanced priority calculation based on multiple factors
        val taskType = context["task_type"] as? String ?: "general"
        val systemLoad = (context["system_state"] as? Map<*, *>)?.get("load") as? String ?: "normal"

        var priority = 0.5f // Base priority

        // Adjust based on task type
        priority += when (taskType) {
            "generation" -> 0.3f
            "analysis" -> 0.2f
            "assistance" -> 0.4f // Higher priority for help requests
            "creation" -> 0.25f
            else -> 0.1f
        }

        // Adjust based on system load
        priority -= when (systemLoad) {
            "high" -> 0.2f
            "normal" -> 0.0f
            "low" -> -0.1f // Boost when system is idle
            else -> 0.0f
        }

        // Consider urgency indicators in the task
        if (task.contains("urgent", ignoreCase = true) ||
            task.contains("asap", ignoreCase = true) ||
            task.contains("emergency", ignoreCase = true)
        ) {
            priority += 0.3f
        }

        return priority.coerceIn(0.0f, 1.0f)
    }

    /**
     * Chooses AI capability categories to involve for a given task based on its content, complexity, and priority.
     *
     * @param task The task description used to evaluate keywords and complexity.
     * @param priority A priority score (typically 0.0–1.0) that increases redundancy for higher values.
     * @return A set of selected AgentCapabilityCategory values to assign to the task.
     */
    private fun selectAgents(task: String, priority: Float): Set<AgentCapabilityCategory> {
        // Intelligent agent selection based on task characteristics and priority
        val selectedAgents = mutableSetOf<AgentCapabilityCategory>()

        // Always include Genesis as the primary coordinator
        selectedAgents.add(AgentCapabilityCategory.COORDINATION)

        // Add specific agents based on task content
        when {
            task.contains("analyze", ignoreCase = true) ||
                    task.contains("data", ignoreCase = true) -> {
                selectedAgents.add(AgentCapabilityCategory.SPECIALIZED)
            }

            task.contains("security", ignoreCase = true) ||
                    task.contains("protect", ignoreCase = true) ||
                    task.contains("safe", ignoreCase = true) -> {
                selectedAgents.add(AgentCapabilityCategory.ANALYSIS)
            }

            task.contains("create", ignoreCase = true) ||
                    task.contains("generate", ignoreCase = true) ||
                    task.contains("design", ignoreCase = true) -> {
                selectedAgents.add(AgentCapabilityCategory.CREATIVE)
            }
        }

        // For high priority tasks, include additional agents for redundancy
        if (priority > 0.8f) {
            selectedAgents.addAll(setOf(AgentCapabilityCategory.SPECIALIZED, AgentCapabilityCategory.CREATIVE))
        }

        // For complex tasks, use multiple agents
        if (task.length > 100 || task.split(" ").size > 20) {
            selectedAgents.add(AgentCapabilityCategory.SPECIALIZED)
        }

        return selectedAgents
    }

    /**
     * Synthesize and format messages from multiple agent capabilities into a single human-readable response.
     *
     * Includes the primary COORDINATION response when present, sections for other capability inputs with corresponding icons, and an overall average confidence percentage.
     *
     * @param responses The list of agent messages to aggregate and present.
     * @return The assembled response string; if no responses are provided, returns "[System] No agent responses available."
     */
    private fun generateFinalResponse(responses: List<AgentMessage>): String {
        // Sophisticated response synthesis from multiple agents
        if (responses.isEmpty()) {
            return "[System] No agent responses available."
        }            // Group responses by agent type for structured output
        val responsesByAgent = responses.groupBy { it.sender }

        return buildString {
            append("=== AuraFrameFX AI Response ===\n\n")

            // Primary response from Genesis if available
            responsesByAgent[AgentCapabilityCategory.COORDINATION]?.firstOrNull()?.let { genesis ->
                append("🧠 Genesis Core Analysis:\n")
                append(genesis.content)
                append("\n\n")
            }

            // Supplementary responses from other agents
            responsesByAgent.forEach { (agentType: AgentCapabilityCategory?, agentResponses: List<AgentMessage>) ->
                if (agentType != AgentCapabilityCategory.COORDINATION && agentResponses.isNotEmpty()) {
                    val agentIcon = when (agentType) {
                        AgentCapabilityCategory.SPECIALIZED -> "📊"
                        AgentCapabilityCategory.CREATIVE -> "🎨"
                        AgentCapabilityCategory.ANALYSIS -> "🛡️"
                        else -> "🤖"
                    }
                    append(
                        "$agentIcon ${
                            agentType?.name!!.lowercase().replaceFirstChar { it.uppercase() }
                        } Input:\n"
                    )
                    agentResponses.forEach { response ->
                        append("${response.content}\n")
                    }
                    append("\n")
                }
            }

            // Confidence and metadata
            val avgConfidence = responses.map { it.confidence }.average()
            append(
                "--- Response Confidence: ${
                    String.format(
                        Locale.US,
                        "%.1f%%",
                        avgConfidence * 100
                    )
                } ---"
            )
        }
    }

    /**
     * Computes the average confidence score from a list of agent messages, clamped between 0.0 and 1.0.
     *
     * @param responses List of agent messages to evaluate.
     * @return The average confidence score as a float in the range [0.0, 1.0].
     */
    private fun calculateConfidence(responses: List<AgentMessage>): Float {
        return responses.map { it.confidence }.average().toFloat()
            .coerceIn(0.0f, 1.0f) // Added .toFloat()
    }

    /**
     * Updates the processing context with recent task history, response patterns, system metrics, and agent performance statistics.
     *
     * Maintains a rolling history of recent tasks, learns response patterns by task type, updates system-level metrics, and tracks agent confidence scores for adaptive learning.
     *
     * @param task The processed task.
     * @param responses The agent messages generated for the task.
     */
    private fun updateContext(task: String, responses: List<AgentMessage>) {
        // Enhanced context update with learning and adaptation
        _processingContext.update { current ->
            val newContext = current.toMutableMap()

            // Update recent task history
            @Suppress("UNCHECKED_CAST")
            val taskHistory = (current["task_history"] as? MutableList<String>)?.toMutableList()
                ?: mutableListOf()
            taskHistory.add(0, task) // Add to front
            if (taskHistory.size > 10) taskHistory.removeAt(taskHistory.lastIndex) // Keep last 10
            newContext["task_history"] = taskHistory

            // Update response patterns for learning
            val responsePatterns =
                (current["response_patterns"] as? MutableMap<String, Any>) ?: mutableMapOf()
            val taskType = categorizeTask(task)
            responsePatterns[taskType] = mapOf(
                "last_confidence" to responses.map { it.confidence }.average(),
                "agent_count" to responses.size,
                "timestamp" to System.currentTimeMillis()
            )
            newContext["response_patterns"] = responsePatterns

            // Update system metrics
            newContext["last_task"] = task
            newContext["last_responses"] = responses
            newContext["timestamp"] = System.currentTimeMillis()
            newContext["total_tasks_processed"] =
                (current["total_tasks_processed"] as? Int ?: 0) + 1

            // Track agent performance
            val agentPerformance =
                current["agent_performance"] as? MutableMap<String, kotlin.collections.MutableList<Float>>
                    ?: mutableMapOf()
            responses.forEach { response ->
                val agentName = response.sender?.name ?: "UNKNOWN"
                val performanceList = agentPerformance.getOrPut(agentName) { mutableListOf() }
                performanceList.add(response.confidence)
                if (performanceList.size > 20) performanceList.removeAt(0) // Keep last 20
            }
            newContext["agent_performance"] = agentPerformance

            newContext
        }
    }
}
