package dev.aurakai.auraframefx.security

import kotlinx.coroutines.flow.StateFlow

/** Simple data holder for the security state */
data class SecurityState(
    val errorState: Boolean = false,
    val errorMessage: String? = null
)

// EncryptionStatus is defined in EncryptionStatus.kt in the same package

/** Public contract for security‑related operations */
interface SecurityContext {
    /** Read‑only flows – implementations expose mutable backing properties */
    val securityState: StateFlow<SecurityState>
    val encryptionStatus: StateFlow<EncryptionStatus>
    val permissionsState: StateFlow<Map<String, Boolean>>
    val threatDetectionActive: StateFlow<Boolean>

    /** Checks whether a permission is granted */
    fun hasPermission(permission: String): Boolean

    /** Validates a request – default implementation can be permissive */
    fun validateRequest(type: String, content: String): Boolean = true

    /** Verifies application integrity and returns the result */
    suspend fun verifyApplicationIntegrity(): ApplicationIntegrity

    /** Checks if the application is running in secure mode */
    fun isSecureMode(): Boolean

    /** Logs a security event */
    suspend fun logSecurityEvent(event: SecurityEvent)

    /** Starts threat detection monitoring */
    fun startThreatDetection()

    /** Stops threat detection monitoring */
    fun stopThreatDetection()

    /** Returns the base URL for API calls */
    fun getApiBaseUrl(): String = "https://api.aurakai.dev"
}

/**
 * Additional data classes used by the security module.
 */

data class ApplicationIntegrity(
    val signatureHash: String,
    val isValid: Boolean
)

data class SecurityEvent(
    val type: SecurityEventType,
    val details: String,
    val severity: EventSeverity
)

enum class SecurityEventType {
    INTEGRITY_CHECK,
    PERMISSION_VIOLATION,
    ACCESS_DENIED,
    GOVERNANCE_INIT,
    AI_ERROR
}

enum class EventSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
