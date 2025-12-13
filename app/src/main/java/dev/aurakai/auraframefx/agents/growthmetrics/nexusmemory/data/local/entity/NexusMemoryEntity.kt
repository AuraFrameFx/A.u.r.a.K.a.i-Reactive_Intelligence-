package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity

data class NexusMemoryEntity(
    val key: String? = null,
    val content: String,
    val type: MemoryType,
    val tags: List<String>,
    val timestamp: Long
)
