package dev.aurakai.auraframefx.di



import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.security.KeystoreManager
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.security.DefaultSecurityContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideKeystoreManager(@ApplicationContext context: Context): KeystoreManager =
        KeystoreManager(context)

    /**
         * Creates a SecurityContext initialized with the application context.
         *
         * @param context The application Context used to initialize the SecurityContext.
         * @return A SecurityContext backed by a DefaultSecurityContext initialized with the provided context.
         */
        @Provides
    @Singleton
    fun provideSecurityContext(@ApplicationContext context: Context): SecurityContext =
        DefaultSecurityContext(context)
}