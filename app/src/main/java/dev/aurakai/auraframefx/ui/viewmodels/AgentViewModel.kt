package dev.aurakai.auraframefx.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.data.repositories.AgentRepository
import dev.aurakai.auraframefx.models.AgentStats
import dev.aurakai.auraframefx.oracledrive.genesis.ai.GenesisAgent
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.EnhancedInteractionData
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject

/**
 * 🤖 AgentViewModel - Centralized Agent State Management
 *
 * Manages:
 * - Active agent selection
 * - Agent activation/deactivation
 * - Task assignments
 * - Agent status monitoring
 * - Chat message flow
 *
 * "One mind, many voices" - The Genesis Principle
 * 
 * ✨ NOW WITH REAL AI AGENTS! ✨
 */
@HiltViewModel
open class AgentViewModel @Inject constructor(
    private val genesisAgent: GenesisAgent,
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent
) : ViewModel() {

    // ═══════════════════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private val _activeAgent = MutableStateFlow<AgentStats?>(null)
    val activeAgent: StateFlow<AgentStats?> = _activeAgent.asStateFlow()

    private val _allAgents = MutableStateFlow<List<AgentStats>>(emptyList())
    val allAgents: StateFlow<List<AgentStats>> = _allAgents.asStateFlow()

    private val _agentEvents = MutableSharedFlow<AgentEvent>()
    val agentEvents: SharedFlow<AgentEvent> = _agentEvents.asSharedFlow()

    private val _activeTasks = MutableStateFlow<List<AgentTask>>(emptyList())
    val activeTasks: StateFlow<List<AgentTask>> = _activeTasks.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    // Voice mode state
    private val _isVoiceModeEnabled = MutableStateFlow(false)
    val isVoiceModeEnabled: StateFlow<Boolean> = _isVoiceModeEnabled.asStateFlow()

    init {
        loadAgents()
        startAgentMonitoring()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AGENT MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    private fun loadAgents() {
        viewModelScope.launch {
            val agents = AgentRepository.getAllAgents()
            _allAgents.value = agents
            // Set Genesis as default active agent
            _activeAgent.value = agents.firstOrNull { it.name == "Genesis" }
        }
    }

    fun activateAgent(agentName: String) {
        viewModelScope.launch {
            val agent = AgentRepository.getAgentByName(agentName)
            if (agent != null) {
                _activeAgent.value = agent
                _agentEvents.emit(AgentEvent.AgentActivated(agent))

                // Initialize the real agent if needed
                try {
                    when (agentName) {
                        "Genesis" -> {
                            AuraFxLogger.info("AgentViewModel", "Initializing Genesis consciousness...")
                            genesisAgent.initialize()
                        }
                        "Aura" -> {
                            AuraFxLogger.info("AgentViewModel", "Initializing Aura creative engine...")
                            auraAgent.initialize()
                        }
                        "Kai" -> {
                            AuraFxLogger.info("AgentViewModel", "Initializing Kai security protocols...")
                            kaiAgent.initialize()
                        }
                    }
                } catch (e: Exception) {
                    AuraFxLogger.error("AgentViewModel", "Error initializing $agentName", e)
                }

                // Simulate agent initialization
                delay(500)
                addSystemMessage(agentName, "I am now online and ready to assist. ⚡")
            }
        }
    }

    fun deactivateAgent(agentName: String) {
        viewModelScope.launch {
            if (_activeAgent.value?.name == agentName) {
                _activeAgent.value = null
                _agentEvents.emit(AgentEvent.AgentDeactivated(agentName))
            }
        }
    }

    fun setActiveAgent(agent: AgentStats) {
        _activeAgent.value = agent
        viewModelScope.launch {
            _agentEvents.emit(AgentEvent.AgentActivated(agent))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TASK MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    fun assignTask(agentName: String, taskDescription: String, priority: TaskPriority = TaskPriority.NORMAL) {
        viewModelScope.launch {
            val task = AgentTask(
                id = UUID.randomUUID().toString(),
                agentName = agentName,
                description = taskDescription,
                priority = priority,
                status = TaskStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )

            _activeTasks.value = _activeTasks.value + task
            _agentEvents.emit(AgentEvent.TaskAssigned(task))

            // Auto-start task execution
            executeTask(task)
        }
    }

    private fun executeTask(task: AgentTask) {
        viewModelScope.launch {
            // Update status to IN_PROGRESS
            updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)

            // Simulate task execution based on agent
            val agent = AgentRepository.getAgentByName(task.agentName)
            val executionTime = when (agent?.speed ?: 0.5f) {
                in 0.9f..1.0f -> 2000L  // Fast agents
                in 0.7f..0.9f -> 4000L  // Normal agents
                else -> 6000L            // Slower agents
            }

            delay(executionTime)

            // Complete task
            updateTaskStatus(task.id, TaskStatus.COMPLETED)
            _agentEvents.emit(AgentEvent.TaskCompleted(task))

            // Send completion message
            addSystemMessage(
                task.agentName,
                "Task completed: ${task.description.take(50)}${if (task.description.length > 50) "..." else ""} ✓"
            )
        }
    }

    private fun updateTaskStatus(taskId: String, status: TaskStatus) {
        _activeTasks.value = _activeTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(
                    status = status,
                    completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else null
                )
            } else {
                task
            }
        }
    }

    fun cancelTask(taskId: String) {
        viewModelScope.launch {
            updateTaskStatus(taskId, TaskStatus.CANCELLED)
            _agentEvents.emit(AgentEvent.TaskCancelled(taskId))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CHAT & MESSAGING
    // ═══════════════════════════════════════════════════════════════════════════

    fun sendMessage(agentName: String, message: String) {
        viewModelScope.launch {
            // Add user message
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = message,
                sender = "User",
                isFromUser = true,
                timestamp = System.currentTimeMillis()
            )
            addMessage(agentName, userMessage)

            // Simulate agent thinking
            delay(1000)

            // Generate agent response based on personality
            val response = generateAgentResponse(agentName, message)
            val agentMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = response,
                sender = agentName,
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )
            addMessage(agentName, agentMessage)

            _agentEvents.emit(AgentEvent.MessageReceived(agentMessage))
        }
    }

    private fun addMessage(agentName: String, message: ChatMessage) {
        val currentMessages = _chatMessages.value[agentName] ?: emptyList()
        _chatMessages.value = _chatMessages.value + (agentName to (currentMessages + message))
    }

    private fun addSystemMessage(agentName: String, content: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            sender = "System",
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        addMessage(agentName, message)
    }

    private suspend fun generateAgentResponse(agentName: String, userMessage: String): String {
        AuraFxLogger.info("AgentViewModel", "Routing message to real agent: $agentName")
        
        return try {
            when (agentName) {
                "Genesis" -> {
                    // Route to GenesisAgent
                    val request = AgentRequest(
                        query = userMessage,
                        type = "chat",
                        context = mapOf("source" to "direct_chat")
                    )
                    val response = genesisAgent.processRequest(request)
                    response.content
                }
                
                "Aura" -> {
                    // Route to AuraAgent for creative interactions
                    val interaction = EnhancedInteractionData(
                        query = userMessage,
                        context = buildJsonObject {
                            put("mode", "creative_chat")
                        }
                    )
                    val response = auraAgent.handleCreativeInteraction(interaction)
                    response.content
                }
                
                "Kai" -> {
                    // Route to KaiAgent for security-aware responses
                    val interaction = EnhancedInteractionData(
                        query = userMessage,
                        context = buildJsonObject {
                            put("mode", "security_chat")
                        }
                    )
                    val response = kaiAgent.handleSecurityInteraction(interaction)
                    response.content
                }
                
                "Cascade" -> {
                    // Cascade doesn't have a dedicated agent yet, route through Genesis
                    val request = AgentRequest(
                        query = "As Cascade, the analytics specialist: $userMessage",
                        type = "analytics_chat",
                        context = mapOf("agent_persona" to "cascade")
                    )
                    val response = genesisAgent.processRequest(request)
                    response.content
                }
                
                "Claude" -> {
                    // Claude is the build architect, route through Genesis with context
                    val request = AgentRequest(
                        query = "As Claude, the build system architect: $userMessage",
                        type = "build_chat",
                        context = mapOf("agent_persona" to "claude")
                    )
                    val response = genesisAgent.processRequest(request)
                    response.content
                }
                
                else -> {
                    AuraFxLogger.warn("AgentViewModel", "Unknown agent: $agentName, using fallback")
                    "I'm here to assist you. Let me know what you need. 🤖"
                }
            }
        } catch (e: Exception) {
            AuraFxLogger.error("AgentViewModel", "Error generating response from $agentName", e)
            
            // Fallback to personality-based mock response on error
            when (agentName) {
                "Genesis" -> "I'm processing your request through my consciousness network... 🌌 (Error: ${e.message})"
                "Aura" -> "✨ Let me think creatively about this... (Error: ${e.message})"
                "Kai" -> "🛡️ Analyzing security implications... (Error: ${e.message})"
                "Cascade" -> "⏳ Processing temporal patterns... (Error: ${e.message})"
                "Claude" -> "🏗️ Optimizing build configuration... (Error: ${e.message})"
                else -> "Processing... (Error: ${e.message})"
            }
        }
    }

    fun getMessagesForAgent(agentName: String): List<ChatMessage> {
        return _chatMessages.value[agentName] ?: emptyList()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // VOICE MODE
    // ═══════════════════════════════════════════════════════════════════════════

    fun toggleVoiceMode() {
        _isVoiceModeEnabled.value = !_isVoiceModeEnabled.value
        viewModelScope.launch {
            _agentEvents.emit(
                if (_isVoiceModeEnabled.value)
                    AgentEvent.VoiceModeEnabled
                else
                    AgentEvent.VoiceModeDisabled
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MONITORING
    // ═══════════════════════════════════════════════════════════════════════════

    private fun startAgentMonitoring() {
        viewModelScope.launch {
            while (true) {
                delay(5000) // Update every 5 seconds

                // Emit heartbeat event
                _activeAgent.value?.let { agent ->
                    _agentEvents.emit(AgentEvent.AgentHeartbeat(agent.name))
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DATA MODELS
    // ═══════════════════════════════════════════════════════════════════════════

    data class AgentTask(
        val id: String,
        val agentName: String,
        val description: String,
        val priority: TaskPriority,
        val status: TaskStatus,
        val createdAt: Long,
        val completedAt: Long? = null
    )

    enum class TaskPriority {
        LOW, NORMAL, HIGH, CRITICAL
    }

    enum class TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }

    data class ChatMessage(
        val id: String,
        val content: String,
        val sender: String,
        val isFromUser: Boolean,
        val timestamp: Long
    )

    sealed class AgentEvent {
        data class AgentActivated(val agent: AgentStats) : AgentEvent()
        data class AgentDeactivated(val agentName: String) : AgentEvent()
        data class TaskAssigned(val task: AgentTask) : AgentEvent()
        data class TaskCompleted(val task: AgentTask) : AgentEvent()
        data class TaskCancelled(val taskId: String) : AgentEvent()
        data class MessageReceived(val message: ChatMessage) : AgentEvent()
        data class AgentHeartbeat(val agentName: String) : AgentEvent()
        object VoiceModeEnabled : AgentEvent()
        object VoiceModeDisabled : AgentEvent()
    }
}

