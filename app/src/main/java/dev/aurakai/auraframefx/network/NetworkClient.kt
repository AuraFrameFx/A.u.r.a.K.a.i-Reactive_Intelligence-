package dev.aurakai.auraframefx.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.aurakai.auraframefx.BuildConfig
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Centralized network client for handling all API communications.
 * Manages OkHttp and Retrofit instances with proper configuration.
 *
 * Note: This class is provided by NetworkModule via factory method.
 * Do not add @Inject or @Singleton annotations here.
 */
class NetworkClient(
    private val context: Context,
    private val authInterceptor: AuthInterceptor,
    private val connectivityManager: NetworkConnectivityManager
) {
    private val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
    private val cacheDir = File(context.cacheDir, "http_cache")
    private val cache = Cache(cacheDir, cacheSize)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            .addInterceptor(authInterceptor)
            .addInterceptor(ConnectivityInterceptor(connectivityManager))
            .addInterceptor(ErrorHandlingInterceptor())

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Creates an API service instance for the specified service interface.
     * @param service The service interface class
     * @return An implementation of the service interface
     */
    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * Clears the HTTP cache.
     */
    fun clearCache() {
        cache.evictAll()
    }

    /**
     * Gets the current cache size in bytes.
     */
    fun getCacheSize(): Long {
        return cache.size()
    }
}
