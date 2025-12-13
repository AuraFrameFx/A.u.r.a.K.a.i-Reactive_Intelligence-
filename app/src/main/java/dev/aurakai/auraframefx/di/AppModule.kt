package dev.aurakai.auraframefx.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.auth.TokenManager
import dev.aurakai.auraframefx.utils.AppCoroutineDispatchers
import dev.aurakai.auraframefx.utils.ApiErrorHandler
import javax.inject.Singleton

// Extension property to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aura_fx_datastore")

/**
 * Dagger Hilt module that provides application-wide dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application's SharedPreferences instance.
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("aura_fx_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Provides the application's DataStore instance for preferences.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Provides the application's coroutine dispatchers.
     */
    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): AppCoroutineDispatchers {
        return AppCoroutineDispatchers()
    }

    /**
     * Provides the TokenManager for handling authentication tokens.
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
        dispatchers: AppCoroutineDispatchers,
    ): TokenManager {
        return TokenManager(context, dispatchers)
    }

    /**
     * Provides the ApiErrorHandler for handling API errors.
     */
    @Provides
    @Singleton
    fun provideApiErrorHandler(
        @ApplicationContext context: Context,
    ): ApiErrorHandler {
        return ApiErrorHandler(context)
    }
}
