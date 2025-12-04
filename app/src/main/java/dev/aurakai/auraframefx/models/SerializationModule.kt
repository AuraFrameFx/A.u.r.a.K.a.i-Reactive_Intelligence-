package dev.aurakai.auraframefx.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val AeGenesisSerializersModule = SerializersModule {
    // Core type serializers - fixed syntax for Kotlin 2.2+
    contextual(Any::class, dev.aurakai.auraframefx.serialization.AnySerializer)
    contextual(Instant::class, dev.aurakai.auraframefx.serialization.InstantSerializer)
}

val AuraFrameSerializersModule = AeGenesisSerializersModule
