package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.state.AppStateManager
import javax.inject.Named
import javax.inject.Singleton

// DataStore property delegate for app state
private val Context.appStateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_state_settings"
)

/**
 * Hilt Module for providing application state related dependencies.
 *
 * This module provides a separate DataStore instance for app-level state
 * (distinct from user preferences) and the AppStateManager that uses it.
 *
 * App State includes:
 * - Onboarding completion status
 * - Feature flags and beta features
 * - App configuration and settings
 * - Agent collaboration session state
 *
 * Note: This is separate from DataStoreModule which provides user preferences.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppStateModule {

    /**
     * Provides a DataStore instance for application state.
     *
     * This DataStore is separate from user preferences and stores app-level state
     * such as onboarding completion, feature flags, and app configuration.
     *
     * @param context Application context provided by Hilt for creating the DataStore
     * @return DataStore<Preferences> for app state management
     */
    @Provides
    @Singleton
    @Named("AppStateDataStore")
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appStateDataStore
    }

    /**
     * Provides an AppStateManager for managing application-level state.
     *
     * @param dataStore The app state DataStore instance
     * @return An AppStateManager instance
     */
    @Provides
    @Singleton
    fun provideAppStateManager(
        @Named("AppStateDataStore") dataStore: DataStore<Preferences>
    ): AppStateManager {
        return AppStateManager()
    }
}
