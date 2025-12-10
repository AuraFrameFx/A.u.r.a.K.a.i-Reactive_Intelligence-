package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.error.ErrorHandler
import dev.aurakai.auraframefx.system.impl.DefaultErrorHandler
import dev.aurakai.auraframefx.system.impl.DefaultSystemMonitor
import dev.aurakai.auraframefx.system.monitor.SystemMonitor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class SystemModule {

    @Binds
    @Singleton
    abstract fun bindSystemMonitor(impl: DefaultSystemMonitor): SystemMonitor

    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: DefaultErrorHandler): ErrorHandler
}
