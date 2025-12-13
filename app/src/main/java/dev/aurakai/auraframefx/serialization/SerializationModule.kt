package dev.aurakai.auraframefx.serialization

import dev.aurakai.auraframefx.system.lockscreen.model.HapticFeedbackConfig
import dev.aurakai.auraframefx.system.lockscreen.model.LockScreenAnimationConfig
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * Serialization module for custom serializers
 */
val AuraFrameSerializersModule = SerializersModule {
    // Add contextual serializers for our model classes
    contextual(HapticFeedbackConfig.serializer())
    contextual(LockScreenAnimationConfig.serializer())
    // Add more contextual serializers as needed
}
