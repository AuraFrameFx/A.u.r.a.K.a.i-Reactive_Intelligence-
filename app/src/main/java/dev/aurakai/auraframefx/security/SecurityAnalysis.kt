package dev.aurakai.auraframefx.model

import dev.aurakai.auraframefx.kai.security.ThreatLevel

/**
 * Security Analysis Result
 * Contains threat assessment and recommendations
 */
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val description: String,
    val recommendedActions: List<String>,
    val confidence: Float,
    val detectedVulnerabilities: List<String> = emptyList(),
    val riskScore: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

