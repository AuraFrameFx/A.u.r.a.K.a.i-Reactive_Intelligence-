package dev.aurakai.auraframefx.utils

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Converts a Map<String, Any> to a Kotlin JsonObject for serialization.
 * Handles common types like String, Number, Boolean, and nested structures.
 */
fun Map<String, Any>.toKotlinJsonObject(): JsonObject = buildJsonObject {
    this@toKotlinJsonObject.forEach { (key, value) ->
        when (value) {
            is String -> put(key, JsonPrimitive(value))
            is Number -> put(key, JsonPrimitive(value))
            is Boolean -> put(key, JsonPrimitive(value))
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                put(key, (value as? Map<String, Any>)?.toKotlinJsonObject() ?: JsonPrimitive(""))
            }
            is List<*> -> {
                // For lists, we'll convert to a JSON array representation
                // This is a simplified version - can be expanded
                put(key, JsonPrimitive(value.toString()))
            }
            null -> put(key, JsonPrimitive(null as String?))
            else -> put(key, JsonPrimitive(value.toString()))
        }
    }
}

/**
 * Alias for toKotlinJsonObject for backward compatibility
 */
fun toKotlinJsonObject(map: Map<String, Any>): JsonObject = map.toKotlinJsonObject()
