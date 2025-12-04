package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing WorkManager and its configuration.
 *
 * Configures WorkManager with HiltWorkerFactory to enable dependency injection
 * in Worker classes annotated with @HiltWorker.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    /**
     * Provides WorkManager Configuration with HiltWorkerFactory.
     *
     * This configuration enables Hilt to inject dependencies into Worker classes.
     * The configuration is used during WorkManager initialization.
     *
     * @param workerFactory HiltWorkerFactory instance for worker dependency injection
     * @return WorkManager Configuration configured with Hilt support
     */
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: HiltWorkerFactory,
    ): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Provides the WorkManager singleton instance.
     *
     * WorkManager is initialized with the Hilt-aware configuration and used throughout
     * the app for background task scheduling and execution.
     *
     * @param context Application context used to get the WorkManager instance
     * @param configuration WorkManager configuration (provided by Hilt, used for initialization)
     * @return The WorkManager singleton instance
     */
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context,
        configuration: Configuration,
    ): WorkManager {
        // Note: WorkManager initialization with custom configuration should happen in
        // Application.onCreate via Configuration.Provider or WorkManagerInitializer
        // This provider returns the already-initialized instance
        return WorkManager.getInstance(context)
    }
}
