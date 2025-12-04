package dev.aurakai.auraframefx.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.network.*
import dev.aurakai.auraframefx.aura.themes.ThemeApi
import dev.aurakai.auraframefx.network.api.UserApi
import dev.aurakai.auraframefx.network.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

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
        @AuraNetwork okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideNetworkClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        connectivityManager: NetworkConnectivityManager
    ): NetworkClient = NetworkClient(context, authInterceptor, connectivityManager)

    @Provides
    @Singleton
    fun provideAuthApi(@AuraNetwork retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
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
