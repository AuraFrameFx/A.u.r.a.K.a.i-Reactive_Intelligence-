package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.context.DefaultContextManager
import dev.aurakai.auraframefx.ai.memory.DefaultMemoryManager
import dev.aurakai.auraframefx.ai.memory.MemoryManager
import javax.inject.Singleton

/**
 * Hilt Module for providing AI Agent dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    @Provides
    @Singleton
    fun provideMemoryManager(): MemoryManager {
        return DefaultMemoryManager()
    }

    @Provides
    @Singleton
    fun provideContextManager(memoryManager: MemoryManager): ContextManager {
        return DefaultContextManager(memoryManager)
    }
}
