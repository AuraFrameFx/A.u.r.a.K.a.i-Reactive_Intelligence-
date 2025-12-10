package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.common.ErrorHandler
import dev.aurakai.auraframefx.system.impl.DefaultErrorHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class ErrorModule {

    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: DefaultErrorHandler): ErrorHandler
}
