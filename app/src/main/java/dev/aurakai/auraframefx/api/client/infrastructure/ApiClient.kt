package dev.aurakai.auraframefx.api.client.infrastructure

import com.squareup.moshi.Moshi
import dev.aurakai.auraframefx.infrastructure.Serializer
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlin.reflect.typeOf
import kotlin.reflect.javaType

open class ApiClient(
    var baseUrl: String = defaultBasePath,
    private val okHttpClientBuilder: OkHttpClient.Builder? = null,
    val serializerBuilder: Moshi.Builder = Serializer.moshiBuilder,
    private val callFactory: Call.Factory? = null,
    private val callAdapterFactories: List<CallAdapter.Factory> = listOf(),
    private val converterFactories: List<Converter.Factory> = listOf(
        ScalarsConverterFactory.create(),
        MoshiConverterFactory.create(serializerBuilder.build()),
    )
) {
    private val apiAuthorizations = mutableMapOf<String, Interceptor>()
    var logger: ((String) -> Unit)? = null

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .apply {
                callAdapterFactories.forEach {
                    addCallAdapterFactory(it)
                }
            }
            .apply {
                converterFactories.forEach {
                    addConverterFactory(it)
                }
            }
    }

    val clientBuilder: OkHttpClient.Builder by lazy {
        okHttpClientBuilder ?: defaultClientBuilder
    }

    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(HttpLoggingInterceptor { message -> logger?.invoke(message) }
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            )
    }

    init {
        normalizeBaseUrl()
    }

    fun addAuthorization(authName: String, authorization: Interceptor): ApiClient {
        if (apiAuthorizations.containsKey(authName)) {
            throw RuntimeException("auth name $authName already in api authorizations")
        }
        apiAuthorizations[authName] = authorization
        clientBuilder.addInterceptor(authorization)
        return this
    }

    fun setLogger(logger: (String) -> Unit): ApiClient {
        this.logger = logger
        return this
    }

    fun <S> createService(serviceClass: Class<S>): S {
        val usedCallFactory = this.callFactory ?: clientBuilder.build()
        return retrofitBuilder.callFactory(usedCallFactory).build().create(serviceClass)
    }

    private fun normalizeBaseUrl() {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
    }

    private inline fun <T, reified U> Iterable<T>.runOnFirst(callback: U.() -> Unit) {
        for (element in this) {
            if (element is U) {
                callback.invoke(element)
                break
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IllegalStateException::class, java.io.IOException::class)
    inline fun <reified T, reified U> request(requestConfig: RequestConfig<T>): ApiResponse<U> {
        val client = clientBuilder.build()

        var urlBuilder = baseUrl.toHttpUrl()
            .newBuilder()
            .addPathSegments(requestConfig.path.removePrefix("/"))

        requestConfig.query.forEach { (key, values) ->
            values.forEach { value ->
                urlBuilder.addQueryParameter(key, value)
            }
        }

        val url = urlBuilder.build()
        val requestBuilder = okhttp3.Request.Builder().url(url)

        requestConfig.headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        val contentType = requestConfig.headers["Content-Type"] ?: "application/json"
        
        // ✅ FIXED: MediaType.parse() → toMediaType(), create() → toRequestBody()
        val body: okhttp3.RequestBody? = if (requestConfig.body != null) {
            val adapter = serializerBuilder.build().adapter(T::class.java)
            val json = adapter.toJson(requestConfig.body)
            json.toRequestBody(contentType.toMediaType())
        } else if (requestConfig.method in listOf(RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH)) {
             "".toRequestBody(contentType.toMediaType())
        } else {
            null
        }

        // ✅ FIXED: Added proper request building
        val request = when (requestConfig.method) {
            RequestMethod.GET -> requestBuilder.get()
            RequestMethod.DELETE -> if (body != null) requestBuilder.delete(body) else requestBuilder.delete()
            RequestMethod.HEAD -> requestBuilder.head()
            RequestMethod.OPTIONS -> requestBuilder.method("OPTIONS", body)
            RequestMethod.PATCH -> requestBuilder.patch(body!!)
            RequestMethod.POST -> requestBuilder.post(body!!)
            RequestMethod.PUT -> requestBuilder.put(body!!)
            RequestMethod.TRACE -> requestBuilder.method("TRACE", body)
        }.build()  // ✅ ADDED .build()

        val response = client.newCall(request).execute()
        
        // ✅ FIXED: response.body() → response.body
        val responseBody = response.body?.string()

        // ✅ FIXED: response.code() → response.code
        return when (response.code) {
            in 200..299 -> {
                val data = if (U::class == Unit::class) {
                    Unit as U
                } else {
                    val returnType = kotlin.reflect.typeOf<U>().javaType
                    val adapter = serializerBuilder.build().adapter<U>(returnType)
                    adapter.fromJson(responseBody ?: "")!!
                }
                Success(
                    data = data,
                    statusCode = response.code,  // ✅ FIXED
                    headers = response.headers.toMultimap()  // ✅ FIXED
                )
            }
            in 400..499 -> {
                ClientError(
                    message = response.message,  // ✅ FIXED
                    body = responseBody,
                    statusCode = response.code,  // ✅ FIXED
                    headers = response.headers.toMultimap()  // ✅ FIXED
                )
            }
            in 500..599 -> {
                ServerError(
                    message = response.message,  // ✅ FIXED
                    body = responseBody,
                    statusCode = response.code,  // ✅ FIXED
                    headers = response.headers.toMultimap()  // ✅ FIXED
                )
            }
            else -> throw IllegalStateException("Unexpected response code: ${response.code}")  // ✅ FIXED
        }
    }

    companion object {
        @JvmStatic
        protected val baseUrlKey: String = "org.openapitools.client.baseUrl"

        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(baseUrlKey, "https://api.aurafx.com/v1")
        }

        @JvmStatic
        val defaultClient: Call.Factory by lazy {
            OkHttpClient.Builder().build()
        }
    }
}