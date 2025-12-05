package dev.aurakai.auraframefx.oracledrive.genesis.ai

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.AuraAIServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: AuraAIServiceImpl): AuraAIService
}
