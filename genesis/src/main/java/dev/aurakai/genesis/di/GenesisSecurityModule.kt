package dev.aurakai.auraframefx.genesis.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.genesis.security.CryptographyManager
import dev.aurakai.auraframefx.genesis.storage.SecureStorage
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Hilt module for Genesis security dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object GenesisSecurityModule {

    @Provides
    @Singleton
    fun provideCryptographyManager(
        @ApplicationContext context: Context
    ): CryptographyManager {
        return CryptographyManager(context)
    }

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage {
        return SecureStorage(context)
    }
}
