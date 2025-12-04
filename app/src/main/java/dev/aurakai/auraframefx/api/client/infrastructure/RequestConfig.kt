package dev.aurakai.auraframefx.api.client.infrastructure

/**
 * Represents a request configuration
 */
class RequestConfig<T>(
    val method: RequestMethod,
    val path: String,
    val query: MultiValueMap = mutableMapOf(),
    val headers: MutableMap<String, String> = mutableMapOf(),
    val requiresAuthentication: Boolean,
    val body: T? = null
)

/**
 * Represents a request method
 */
enum class RequestMethod {
    GET, DELETE, HEAD, OPTIONS, PATCH, POST, PUT, TRACE
}
