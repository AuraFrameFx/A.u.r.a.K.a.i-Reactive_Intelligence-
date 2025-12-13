package dev.aurakai.auraframefx.ai.memory

import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryStats

/**
 * Interface for memory management operations
 */
interface MemoryManagerInterface {
    fun storeMemory(key: String, value: String)
    fun retrieveMemory(key: String): String?
    fun storeInteraction(prompt: String, response: String)
    fun searchMemories(query: String): List<MemoryEntry>
    fun clearMemories()
    fun getMemoryStats(): MemoryStats
}
