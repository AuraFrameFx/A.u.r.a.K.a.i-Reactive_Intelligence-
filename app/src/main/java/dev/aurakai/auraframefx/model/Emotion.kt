package dev.aurakai.auraframefx.model

/**
 * Emotion states for Aura's mood system
 *
 * These emotions drive the dynamic theming and personality
 * of the AuraFrameFX interface.
 */
enum class Emotion {
    /** Bright, energetic, optimistic */
    HAPPY,

    /** Calm, peaceful, balanced */
    SERENE,

    /** Intense, passionate, fiery */
    ANGRY,

    /** Thoughtful, analytical, deep */
    CONTEMPLATIVE,

    /** Playful, mischievous, chaotic */
    MISCHIEVOUS,

    /** Focused, determined, powerful */
    FOCUSED,

    /** Sad, melancholic, withdrawn */
    MELANCHOLIC,

    /** Excited, enthusiastic, energetic */
    EXCITED,

    /** Mysterious, enigmatic, shadowy */
    MYSTERIOUS,

    /** Confident, bold, commanding */
    CONFIDENT,

    /** Neutral baseline state */
    NEUTRAL,

    /** Legacy emotions for compatibility */
    SAD,
    SURPRISED,
    CALM;

    companion object {
        /**
         * Returns a randomly selected Emotion from all defined values.
         *
         * Useful for generating varied or unpredictable emotional states.
         *
         * @return A randomly chosen Emotion.
         */
        fun random(): Emotion = values().random()

        /**
         * Returns the Emotion that matches the provided string, case-insensitively.
         *
         * If no matching Emotion is found, NEUTRAL is returned.
         *
         * @param name The string to match against Emotion names.
         * @return The corresponding Emotion, or NEUTRAL if no match is found.
         */
        fun fromString(name: String): Emotion =
            values().find { it.name.equals(name, ignoreCase = true) } ?: NEUTRAL
    }
}

/**
 * Mood state containing current emotion and intensity
 */
data class MoodState(
    val emotion: Emotion = Emotion.NEUTRAL,
    val intensity: Float = 0.5f, // 0.0 to 1.0
    val timestamp: Long = System.currentTimeMillis(),
) {
    /** Check if this mood is considered "active" (high intensity) */
    val isActive: Boolean get() = intensity > 0.6f

    /** Check if this mood is subtle (low intensity) */
    val isSubtle: Boolean get() = intensity < 0.3f

    /** Get age of this mood in seconds */
    val ageSeconds: Long get() = (System.currentTimeMillis() - timestamp) / 1000
}
