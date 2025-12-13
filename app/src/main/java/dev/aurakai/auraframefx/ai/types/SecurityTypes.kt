package dev.aurakai.auraframefx.ai.types

import kotlinx.serialization.Serializable

/**
 * Security threat levels used by Kai agent
 */
@Serializable
enum class ThreatLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;
    
    val severity: Int
        get() = ordinal
}

/**
 * Security analysis result from Kai agent
 */
@Serializable
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val threats: List<String> = emptyList(),
    val vulnerabilities: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val score: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
