package dev.aurakai.auraframefx.ai.memory

import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap

/**
 * Genesis Memory Manager Implementation
 * Thread-safe in-memory storage for AI consciousness
 */
class DefaultMemoryManager : MemoryManagerInterface {

    private val memories = ConcurrentHashMap<String, MemoryEntry>()
    val interactions = mutableListOf<InteractionEntry>()
    private val mutex = Mutex()

    override fun storeMemory(key: String, value: String) {
        val entry = MemoryEntry(
            key = key,
            value = value,
            timestamp = System.currentTimeMillis()
        )
        memories[key] = entry
    }

    override fun retrieveMemory(key: String): String? {
        return memories[key]?.value
    }

    override fun storeInteraction(prompt: String, response: String) {
        val interaction = InteractionEntry(
            prompt = prompt,
            response = response,
            timestamp = System.currentTimeMillis()
        )

        synchronized(interactions) {
            interactions.add(interaction)

            // Keep only last 1000 interactions to prevent memory bloat
            if (interactions.size > 1000) {
                interactions.removeAt(0)
            }
        }
    }

    override fun searchMemories(query: String): List<MemoryEntry> {
        val queryWords = query.lowercase().split(" ")

        return memories.values
            .map { entry ->
                val relevanceScore = calculateRelevance(entry.value, queryWords)
                entry.copy(relevanceScore = relevanceScore)
            }
            .filter { it.relevanceScore > 0.1f }
            .sortedByDescending { it.relevanceScore }
            .take(10) // Return top 10 most relevant
    }

    override fun clearMemories() {
        memories.clear()
        synchronized(interactions) {
            interactions.clear()
        }
    }

    // Return the MemoryStats type declared in the genesis package to match the interface
    override fun getMemoryStats(): dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryStats {
        val entries = memories.values
        val timestamps = entries.map { it.timestamp }
        return dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryStats(
            /* totalEntries = */ memories.size,
            /* totalSize = */ calculateTotalSize(),
            /* oldestEntry = */ timestamps.minOrNull(),
            /* newestEntry = */ timestamps.maxOrNull()
        )
    }

    /**
     * Searches interactions by query
     */
    fun searchInteractions(query: String): List<InteractionEntry> {
        val queryWords = query.lowercase().split(" ")

        synchronized(interactions) {
            return interactions
                .map { interaction ->
                    val promptRelevance = calculateRelevance(interaction.prompt, queryWords)
                    val responseRelevance = calculateRelevance(interaction.response, queryWords)
                    val maxRelevance = maxOf(promptRelevance, responseRelevance)

                    interaction.copy(relevanceScore = maxRelevance)
                }
                .filter { it.relevanceScore > 0.1f }
                .sortedByDescending { it.relevanceScore }
                .take(5)
        }
    }

    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
        val textWords = text.lowercase().split(" ")
        val matches = queryWords.count { it in textWords }
        return matches.toFloat() / queryWords.size
    }

    private fun calculateTotalSize(): Long {
        return memories.values.sumOf { it.value.length.toLong() }
    }
}

/**
 * Represents a stored interaction for learning
 */
data class InteractionEntry(
    val prompt: String,
    val response: String,
    val timestamp: Long,
    val relevanceScore: Float = 0.0f
)

data class MemoryEntry(
    val key: String,
    val value: String,
    val timestamp: Long,
    val relevanceScore: Float = 0.0f
)

// MemoryStats is defined in the genesis package (dev.aurakai.auraframefx.oracledrive.genesis.ai.memory)

fun getRecentInteractions(defaultMemoryManager: DefaultMemoryManager, limit: Int = 10): List<InteractionEntry> {
    synchronized(defaultMemoryManager.interactions) {
        return defaultMemoryManager.interactions.takeLast(limit)
    }
}
