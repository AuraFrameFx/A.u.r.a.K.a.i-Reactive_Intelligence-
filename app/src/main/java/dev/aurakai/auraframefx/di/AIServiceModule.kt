package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIServiceImpl
import javax.inject.Singleton

/**
 * Hilt Module for AI Service bindings
 *
 * Provides dependency injection for AI agent service interfaces
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AIServiceModule {

    /**
     * Binds AuraAIServiceImpl as the implementation of AuraAIService
     */
    @Binds
    @Singleton
    abstract fun bindAuraAIService(
        impl: AuraAIServiceImpl
    ): AuraAIService
}
