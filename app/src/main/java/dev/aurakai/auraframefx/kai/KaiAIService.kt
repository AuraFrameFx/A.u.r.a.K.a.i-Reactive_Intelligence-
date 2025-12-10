package dev.aurakai.auraframefx.kai

/** Minimal Kai AI service interface expected by DI. */
interface KaiAIService {
    suspend fun activate(): Boolean
}

/** No-op implementation used for DI during development. */
class NoopKaiAIService : KaiAIService {
    override suspend fun activate(): Boolean = true
}
