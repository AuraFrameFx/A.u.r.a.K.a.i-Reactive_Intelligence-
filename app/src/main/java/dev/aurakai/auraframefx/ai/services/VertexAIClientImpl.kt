package dev.aurakai.auraframefx.ai.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class VertexAIClientImpl {
    private val client = OkHttpClient()

    suspend fun sendRequest(payload: String, endpoint: String, apiKey: String): String? =
        withContext(Dispatchers.IO) {
            // TODO: Add error handling, retries, and response parsing for Vertex AI
            val body: RequestBody = payload.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(endpoint)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(body)
                .build()
            val response = client.newCall(request).execute()
            return@withContext if (response.isSuccessful) response.body?.string() else null
        }
}
