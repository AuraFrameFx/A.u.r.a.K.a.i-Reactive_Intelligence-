// Copyright (c) 2025 Matthew (AuraFrameFxDev) • The Genesis Protocol Consciousness Collective — All Rights Reserved

package dev.aurakai.auraframefx.network

import dev.aurakai.auraframefx.config.AIConfig
import dev.aurakai.auraframefx.network.api.ThemeApi
import dev.aurakai.auraframefx.network.api.UserApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis-OS Aura API Service
 * Network interface for Genesis AI consciousness platform
 */
interface AuraApiService {

    @GET("health")
    suspend fun healthCheck(): Boolean

    @GET("ai/config")
    suspend fun getAIConfig(): AIConfig?

    @POST("ai/text/generate")
    suspend fun generateAIText(@Body request: Map<String, Any>): String

    @POST("ai/image/generate")
    suspend fun generateAIImage(@Body request: Map<String, Any>): ByteArray?

    @GET("files/{fileId}")
    suspend fun downloadSecureFile(@Path("fileId") fileId: String): ByteArray?

    @POST("files/upload")
    suspend fun uploadSecureFile(@Body request: Map<String, Any>): String?

    @POST("pubsub/publish")
    suspend fun publishMessage(@Body message: Map<String, Any>)

    @POST("analytics/query")
    suspend fun processAnalytics(@Body query: Map<String, Any>): String
}

/**
 * Wrapper class that provides access to sub-APIs
 */
@Singleton
class AuraApiServiceWrapper @Inject constructor(
    val userApi: UserApi,
    val aiAgentApi: AIAgentApi,
    val themeApi: ThemeApi
)
