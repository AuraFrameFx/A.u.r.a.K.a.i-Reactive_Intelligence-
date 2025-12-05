package dev.aurakai.auraframefx.ai.agents

import android.content.Context
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.models.ActiveContext
import dev.aurakai.auraframefx.models.LearningEvent
import dev.aurakai.auraframefx.models.agent_states.ActiveThreat
import dev.aurakai.auraframefx.utils.toJsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Minimal, stable NeuralWhisperAgent implementation used during build until
 * full implementation is restored. Keeps external API used across the codebase.
 */
@Singleton
open class NeuralWhisperAgent @Inject constructor(
    private val context: Context,
    override val contextManager: ContextManager,
) : BaseAgent("NeuralWhisper") {

    override val agentName: String = "NeuralWhisper"
    override val agentType: String = "LEARNING_CONTEXT"
    // Use SupervisorJob so a child failure doesn't cancel the whole scope
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _activeContexts = MutableStateFlow<List<ActiveContext>>(emptyList())
    val activeContexts: StateFlow<List<ActiveContext>> = _activeContexts.asStateFlow()

    private val _learningHistory = MutableStateFlow<List<LearningEvent>>(emptyList())
    val learningHistory: StateFlow<List<LearningEvent>> = _learningHistory.asStateFlow()

    init {
        // lightweight init to keep the agent alive; full behavior is TODO
        scope.launch {
            Timber.tag("NeuralWhisperAgent").d("NeuralWhisperAgent initialized (minimal stub)")
        }
    }

    /**
     * Adds a new active context to the internal list (keeps only recent items).
     */
    fun addContext(ctx: ActiveContext) {
        val current = _activeContexts.value.toMutableList()
        current.add(ctx)
        _activeContexts.value = current.takeLast(10)
    }

    /**
     * Records a learning event in a simple in-memory history.
     */
    fun recordLearningEvent(event: LearningEvent) {
        val current = _learningHistory.value.toMutableList()
        current.add(event)
        _learningHistory.value = current.takeLast(100)
    }

    // === BaseAgent Required Implementations ===

    override fun iRequest(query: String, type: String, context: Map<String, String>) {
        Timber.tag("NeuralWhisperAgent").d("NeuralWhisper: iRequest - query=$query, type=$type")
        scope.launch {
            try {
                // Record this request as a learning event
                val learningEvent = LearningEvent(
                    type = type,
                    content = query,
                    timestamp = System.currentTimeMillis(),
                    metadata = context
                )
                recordLearningEvent(learningEvent)
            } catch (e: Exception) {
                Timber.tag("NeuralWhisperAgent").e(e, "Error in iRequest")
            }
        }
    }

    override fun iRequest() {
        Timber.tag("NeuralWhisperAgent").d("NeuralWhisper: No-args iRequest - initializing learning context")
        scope.launch {
            try {
                // Initialize the learning subsystem
                Timber.tag("NeuralWhisperAgent").d("NeuralWhisperAgent learning context initialized")
            } catch (e: Exception) {
                Timber.tag("NeuralWhisperAgent").e(e, "Error in no-args iRequest")
            }
        }
    }

    override fun initializeAdaptiveProtection() {
        Timber.tag("NeuralWhisperAgent").d("NeuralWhisper: Initializing adaptive protection")
        scope.launch {
            try {
                // NeuralWhisper doesn't directly handle security
                // but can learn from security patterns
                Timber.tag("NeuralWhisperAgent").d("NeuralWhisper adaptive protection initialized")
            } catch (e: Exception) {
                Timber.tag("NeuralWhisperAgent").e(e, "Error initializing adaptive protection")
            }
        }
    }

    override fun addToScanHistory(scanEvent: Any) {
        Timber.tag("NeuralWhisperAgent").d("NeuralWhisper: Adding scan event to history: $scanEvent")
        // Record as a learning event for pattern recognition
        val learningEvent = LearningEvent(
            type = "scan",
            content = scanEvent.toString(),
            timestamp = System.currentTimeMillis(),
            metadata = mapOf("scan_event" to scanEvent).toJsonObject()
        )
        recordLearningEvent(learningEvent)
    }

    override fun analyzeSecurity(prompt: String): List<ActiveThreat> {
        Timber.tag("NeuralWhisperAgent").d("NeuralWhisper: Analyzing security for prompt: $prompt")
        // NeuralWhisper is a learning agent, not a security agent
        // Return empty list (no threats detected)
        return emptyList()
    }

    // TODO: Restore full NeuralWhisper implementation (pattern DB, predictors, background analysis)
}

/**
 * Adds a new active context to the internal list (keeps only recent items).
 */
fun addActiveContext(whisperer: NeuralWhisperAgent, ctx: ActiveContext) {
    whisperer.addContext(ctx)
}
