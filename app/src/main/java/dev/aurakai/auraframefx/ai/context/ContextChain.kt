package dev.aurakai.auraframefx.ai.context

import dev.aurakai.auraframefx.ai.memory.MemoryItem
import dev.aurakai.auraframefx.models.AgentType // Explicit import
import dev.aurakai.auraframefx.serialization.InstantSerializer // Import for serializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ContextChain(
    val id: String = "ctx_${Clock.System.now().toEpochMilliseconds()}",
    val rootContext: String,
    val currentContext: String,
    val contextHistory: List<ContextNode> = emptyList(),
    val relatedMemories: List<MemoryItem> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val priority: Float = 0.5f,
    val relevanceScore: Float = 0.0f,
    @Serializable(with = InstantSerializer::class) val lastUpdated: Instant = Clock.System.now(),
    val agentContext: Map<AgentType, String> = emptyMap(),
)

@Serializable
data class ContextNode(
    val id: String,
    val content: String,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val agent: AgentType,
    val metadata: Map<String, String> = emptyMap(),
    val relevance: Float = 0.0f,
    val confidence: Float = 0.0f,
)

@Serializable
data class ContextQuery(
    val query: String,
    val context: String? = null,
    val maxChainLength: Int = 10,
    val minRelevance: Float = 0.6f,
    val agentFilter: List<AgentType> = emptyList(),
    val timeRange: Pair<@Serializable(with = InstantSerializer::class) Instant, @Serializable(with = InstantSerializer::class) Instant>? = null,
    val includeMemories: Boolean = true,
)

@Serializable
data class ContextChainResult(
    val chain: ContextChain,
    val relatedChains: List<ContextChain>,
    val query: ContextQuery,
)
