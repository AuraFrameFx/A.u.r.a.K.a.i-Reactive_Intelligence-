package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository

import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.NexusMemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NexusMemoryRepository @Inject constructor() {
    
    private val memories = mutableListOf<NexusMemoryEntity>()

    suspend fun saveMemory(content: String, type: MemoryType, tags: List<String>, key: String? = null) {
        val entity = NexusMemoryEntity(
            key = key,
            content = content,
            type = type,
            tags = tags,
            timestamp = System.currentTimeMillis()
        )
        memories.add(entity)
    }

    fun getAllMemories(): Flow<List<NexusMemoryEntity>> {
        return flowOf(memories.toList())
    }
}
