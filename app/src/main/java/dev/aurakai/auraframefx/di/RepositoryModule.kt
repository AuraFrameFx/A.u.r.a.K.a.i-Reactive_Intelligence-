package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.cascade.trinity.TrinityRepository
import javax.inject.Singleton

/**
 * Hilt module that provides repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides the [TrinityRepository] implementation.
     */
    @Provides
    @Singleton
    fun provideTrinityRepository(
        apiService: AuraApiServiceWrapper
    ): TrinityRepository = TrinityRepository(apiService,)
}
