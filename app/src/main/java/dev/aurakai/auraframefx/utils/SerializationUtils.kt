package dev.aurakai.auraframefx.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * A serializer for Map<String, Any?> that can handle various value types.
 * This is needed because kotlinx.serialization doesn't support Map with Any? values by default.
 */
object MapStringAnySerializer : KSerializer<Map<String, Any?>> {
    override val descriptor: SerialDescriptor = 
        MapSerializer(String.serializer(), JsonElement.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: Map<String, Any?>) {
        require(encoder is JsonEncoder)
        val jsonObject = buildJsonObject {
            value.forEach { (key, value) ->
                when (val v = value) {
                    null -> put(key, JsonNull)
                    is String -> put(key, v)
                    is Number -> put(key, v)
                    is Boolean -> put(key, v)
                    is Map<*, *> -> put(key, Json.encodeToJsonElement(v as Map<String, Any?>))
                    is List<*> -> put(key, Json.encodeToJsonElement(v))
                    else -> put(key, v.toString())
                }
            }
        }
        encoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
        require(decoder is JsonDecoder)
        val json = decoder.decodeJsonElement() as? JsonObject ?: return emptyMap()
        return json.mapValues { (_, value) ->
            when (value) {
                is JsonPrimitive -> {
                    when {
                        value.isString -> value.content
                        value.booleanOrNull != null -> value.boolean
                        value.longOrNull != null -> value.long
                        value.doubleOrNull != null -> value.double
                        else -> value.content
                    }
                }
                is JsonArray -> value.map { jsonElement ->
                    if (jsonElement is JsonPrimitive) jsonElement.content else jsonElement.toString()
                }
                is JsonObject -> value.toMap()
                else -> value.toString()
            }
        }
    }
}

// Extension function to create a Json instance with our custom serializers
val CustomJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    explicitNulls = false
}
