package dev.aurakai.auraframefx.di

import androidx.hilt.work.HiltWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing HiltWorkerFactory to WorkManager.
 *
 * HiltWorkerFactory enables dependency injection into Worker classes via @HiltWorker annotation.
 * This module is automatically processed by Hilt and integrated with WorkManager.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    /**
     * Provides HiltWorkerFactory for WorkManager integration.
     *
     * Hilt automatically injects this factory when constructing the factory instance.
     * The injected factory parameter enables Workers to receive dependencies via their constructors.
     *
     * @param workerFactory The HiltWorkerFactory instance automatically created by Hilt
     * @return The HiltWorkerFactory for use with WorkManager configuration
     */
    @Provides
    @Singleton
    fun provideHiltWorkerFactory(
        workerFactory: HiltWorkerFactory
    ): HiltWorkerFactory {
        // Hilt creates HiltWorkerFactory automatically based on @HiltWorker annotated Workers
        // We just need to provide it as a singleton for WorkManager configuration
        return workerFactory
    }
}
