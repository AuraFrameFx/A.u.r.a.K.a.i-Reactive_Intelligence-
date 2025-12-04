// Kotlin
package dev.aurakai.auraframefx.infrastructure

import com.squareup.moshi.Moshi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import dev.aurakai.auraframefx.api.client.infrastructure.ByteArrayAdapter
import dev.aurakai.auraframefx.ui.adapters.UUIDAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Serializer {
    @Deprecated(
        "Use Serializer.kotlinxSerializationAdapters instead",
        replaceWith = ReplaceWith("Serializer.kotlinxSerializationAdapters"),
        level = DeprecationLevel.ERROR
    )
    @JvmStatic
    val kotlinSerializationAdapters: SerializersModule
        get() {
            return kotlinxSerializationAdapters
        }

    private var isAdaptersInitialized = false

    // Keep an empty module here until concrete KSerializer implementations are available
    @JvmStatic
    val kotlinxSerializationAdapters: SerializersModule by lazy {
        isAdaptersInitialized = true
        SerializersModule {
            // Add contextual serializers here when their KSerializer implementations are present.
            apply(kotlinxSerializationAdaptersConfiguration)
        }
    }

    var kotlinxSerializationAdaptersConfiguration: SerializersModuleBuilder.() -> Unit = {}
        set(value) {
            check(!isAdaptersInitialized) {
                "Cannot configure kotlinxSerializationAdaptersConfiguration after kotlinxSerializationAdapters has been initialized."
            }
            field = value
        }

    @Deprecated(
        "Use Serializer.kotlinxSerializationJson instead",
        replaceWith = ReplaceWith("Serializer.kotlinxSerializationJson"),
        level = DeprecationLevel.ERROR
    )
    @JvmStatic
    val jvmJson: Json
        get() {
            return kotlinxSerializationJson
        }

    private var isJsonInitialized = false

    @JvmStatic
    val kotlinxSerializationJson: Json by lazy {
        isJsonInitialized = true
        Json {
            serializersModule = kotlinxSerializationAdapters
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true

            apply(kotlinxSerializationJsonConfiguration)
        }
    }

    var kotlinxSerializationJsonConfiguration: JsonBuilder.() -> Unit = {}
        set(value) {
            check(!isJsonInitialized) {
                "Cannot configure kotlinxSerializationJsonConfiguration after kotlinxSerializationJson has been initialized."
            }
            field = value
        }

    // Minimal Moshi setup using adapters that exist in the project.
    @JvmStatic
    val moshiBuilder: Moshi.Builder = Moshi.Builder()
        .add(UUIDAdapter())
        .add(ByteArrayAdapter())
        .add(KotlinJsonAdapterFactory())

    @JvmStatic
    val moshi: Moshi by lazy {
        moshiBuilder.build()
    }
}


