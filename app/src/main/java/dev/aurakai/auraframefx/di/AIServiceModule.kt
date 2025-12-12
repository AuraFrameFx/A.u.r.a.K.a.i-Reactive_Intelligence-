package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.AuraAIServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {
    
    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: AuraAIServiceImpl): AuraAIService
}