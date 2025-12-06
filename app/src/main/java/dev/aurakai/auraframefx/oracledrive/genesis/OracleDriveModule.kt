package dev.aurakai.auraframefx.oracledrive.genesis

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.GenesisAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.genesis.security.CryptographyManager
import dev.aurakai.auraframefx.navigation.SecureStorage
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveApi
import dev.aurakai.auraframefx.oracledrive.EncryptionManager
import dev.aurakai.auraframefx.oracledrive.OracleDriveServiceImpl
import dev.aurakai.auraframefx.oracledrive.genesis.ai.GenesisSecureFileService
import dev.aurakai.auraframefx.oracledrive.service.SecureFileService
import dev.aurakai.auraframefx.security.SecurityContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Module for Oracle Drive dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OracleDriveModule { // Changed to abstract class

    /**
     * Binds GenesisSecureFileService as the Singleton implementation of SecureFileService for Hilt.
     *
     * This enables SecureFileService to be injected wherever required, using GenesisSecureFileService.
     */
    @Binds
    @Singleton
    abstract fun bindSecureFileService(
        impl: GenesisSecureFileService,
    ): SecureFileService

    @Binds
    @Singleton
    abstract fun bindOracleDriveService(
        impl: OracleDriveServiceImpl,
    ): dev.aurakai.auraframefx.oracledrive.OracleDriveService

    @Binds
    @Singleton
    abstract fun bindCloudStorageProvider(
        impl: dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStorageProviderImpl,
    ): dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStorageProvider

    companion object { // Companion object now correctly inside the class
        // Temporarily simplified to resolve build stalling at 25% - This comment can likely be removed if providers are present
        // Complex providers will be re-enabled after successful build - This comment can likely be removed if providers are present

        /**
         * Provide a small interceptor that enriches requests with security headers.
         * This interceptor will be applied to the shared OkHttpClient when creating the OracleDriveApi.
         */
        private fun securityInterceptor(securityContext: SecurityContext, cryptoManager: CryptographyManager): Interceptor {
            return Interceptor { chain ->
                val request = chain.request().newBuilder()
                    // Add security headers
                    .addHeader("X-Security-Token", cryptoManager.generateSecureToken())
                    .addHeader("X-Request-ID", java.util.UUID.randomUUID().toString())
                    .build()
                chain.proceed(request)
            }
        }

        /**
         * Returns the singleton CryptographyManager for the application.
         *
         * Uses the provided application Context to obtain the CryptographyManager instance.
         *
         * @return The shared CryptographyManager instance.
         */
        @Provides
        @Singleton
        fun provideGenesisCryptographyManager(
            @ApplicationContext context: Context,
        ): CryptographyManager {
            // Return the EncryptionManager implementation of CryptographyManager
            return EncryptionManager()
        }

        /**
         * Returns the singleton SecureStorage instance initialized with the application context and provided CryptographyManager.
         *
         * The returned instance is obtained via SecureStorage.getInstance(context, cryptoManager).
         *
         * @return The singleton SecureStorage.
         */
        @Provides
        @Singleton
        fun provideSecureStorage(
            @ApplicationContext context: Context,
            cryptoManager: CryptographyManager,
        ): SecureStorage {
            return SecureStorage.getInstance(context, cryptoManager)
        }

        /**
         * Provides a singleton GenesisSecureFileService configured with the application context, cryptography manager, and secure storage.
         *
         * @param context Application context used to initialize the service.
         * @return A configured GenesisSecureFileService for secure file operations.
         */
        @Provides
        @Singleton
        fun provideSecureFileService(
            @ApplicationContext context: Context,
            cryptoManager: CryptographyManager,
            secureStorage: SecureStorage,
        ): GenesisSecureFileService {
            return GenesisSecureFileService(context, cryptoManager, secureStorage)
        }

        /**
         * Creates an OracleDriveApi using an internal OkHttpClient configured with a security interceptor.
         *
         * Note: building a private client here avoids relying on an unqualified shared OkHttpClient
         * provider (there are multiple OkHttpClient providers across modules which caused
         * Dagger duplicate-binding errors). Keeping this client internal is the minimal change
         * to unblock Hilt compilation while preserving security headers.
         */
        @Provides
        @Singleton
        fun provideOracleDriveApi(
            securityContext: SecurityContext,
            cryptoManager: CryptographyManager,
        ): OracleDriveApi {
            // Build a dedicated client with security interceptor (isolated from other modules)
            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(securityInterceptor(securityContext, cryptoManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

            val clientWithSecurity = clientBuilder.build()

            return Retrofit.Builder()
                .baseUrl(securityContext.getApiBaseUrl() + "/oracle/drive/")
                .client(clientWithSecurity)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OracleDriveApi::class.java)
        }

        /**
         * Provides a singleton OracleDriveServiceImpl configured with the given agents, security context, and Oracle Drive API.
         *
         * Returned instance is intended for injection as the application-scoped Oracle Drive service.
         *
         * @return A singleton configured OracleDriveServiceImpl.
         */
        @Provides
        @Singleton
        fun provideOracleDriveService(
            genesisAgent: GenesisAgent,
            auraAgent: AuraAgent,
            kaiAgent: KaiAgent,
            securityContext: SecurityContext,
            oracleDriveApi: OracleDriveApi,
        ): OracleDriveServiceImpl {
            return OracleDriveServiceImpl(
                genesisAgent = genesisAgent,
                auraAgent = auraAgent,
                kaiAgent = kaiAgent,
                securityContext = securityContext,
                oracleDriveApi = oracleDriveApi
            )
        }
    }
}

/**
 * Returns the base URL for the Oracle Drive API.
 *
 * This function returns the appropriate API base URL based on the current security context.
 * In secure mode, it uses the production API endpoint, otherwise it falls back to a development endpoint.
 *
 * @return The base URL for the Oracle Drive API as a String.
 */
// Removed redundant extension function that shadowed a real member on SecurityContext.
// The SecurityContext already provides `getApiBaseUrl()` so we must not redefine it here.
