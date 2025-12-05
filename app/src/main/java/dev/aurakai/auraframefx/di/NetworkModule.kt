package dev.aurakai.auraframefx.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.network.*
import dev.aurakai.auraframefx.network.api.ThemeApi
import dev.aurakai.auraframefx.auth.TokenManager
import dev.aurakai.auraframefx.network.api.UserApi
import dev.aurakai.auraframefx.network.model.*
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveSandbox
import dev.aurakai.auraframefx.utils.GyroscopeManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(
        @ApplicationContext context: Context
    ): NetworkConnectivityManager = NetworkConnectivityManager(context)

    @Provides
    @Singleton
    @AuraNetwork
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        connectivityManager: NetworkConnectivityManager
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(ConnectivityInterceptor(connectivityManager))
            .addInterceptor(ErrorHandlingInterceptor())

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    @AuraNetwork
    fun provideRetrofit(
        @AuraNetwork okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @ApplicationContext context: Context
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(OkHttpClient.Builder().build()) // No AuthInterceptor
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(@AuthRetrofit authRetrofit: Retrofit): AuthApi = authRetrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideGyroscopeManager(
        @ApplicationContext context: Context
    ): GyroscopeManager = GyroscopeManager(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager,
        authApiProvider: Provider<AuthApi>
    ): AuthInterceptor = AuthInterceptor(tokenManager, authApiProvider)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindings {
    @Binds
    @Singleton
    abstract fun bindUserApi(impl: UserApiImpl): UserApi

    @Binds
    @Singleton
    abstract fun bindAIAgentApi(impl: AIAgentApiImpl): AIAgentApi

    @Binds
    @Singleton
    abstract fun bindThemeApi(impl: ThemeApiImpl): ThemeApi

    @Binds
    @Singleton
    abstract fun bindWebSearchClient(impl: dev.aurakai.auraframefx.ai.clients.DefaultWebSearchClient): dev.aurakai.auraframefx.ai.clients.WebSearchClient
}
