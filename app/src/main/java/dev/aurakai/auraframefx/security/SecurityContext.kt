package dev.aurakai.auraframefx.security // Updated package name

// SecretKeyFactory, PBEKeySpec, SecretKeySpec are removed as they are related to PBKDF2
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.ThreatLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SecurityContext manages the security aspects of the AuraFrameFx system.
 * This class is tied to the KAI agent persona and handles all security-related operations.
 */
import dev.aurakai.auraframefx.core.logging.TimberInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityContext @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keystoreManager: KeystoreManager, // Added KeystoreManager
    private val timberInitializer: TimberInitializer,
) {
    companion object {
        private const val TAG = "SecurityContext"
        private const val THREAT_DETECTION_INTERVAL_MS = 30000L // 30 seconds

        // ENCRYPTION_ALGORITHM is defined in KeystoreManager as AES_MODE or can be a shared constant
        // SECRET_KEY_ALGORITHM, KEY_LENGTH, ITERATION_COUNT removed (PBKDF2 specific)
        private const val AES_ALGORITHM_WITH_PADDING =
            "AES/CBC/PKCS7Padding" // Re-added for direct Cipher.getInstance calls
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _securityState = MutableStateFlow(SecurityState())
    val securityState: StateFlow<SecurityState> = _securityState.asStateFlow()

    /**
     * Placeholder for content validation logic.
     *
     * In its current form, this method accepts all content without performing any checks. Intended to be extended to detect policy violations or security threats in production environments.
     */
    fun validateContent(content: String) {
        // TODO: Implement real validation logic
        // For now, always allow
    }

    /**
     * Validates image data for security compliance.
     *
     * Currently a stub that logs the image data size and allows all input. Intended for future implementation of image validation logic.
     */
    fun validateImageData(imageData: ByteArray) {
        // TODO: Implement real image validation logic
        // For now, always allow
        Log.d(TAG, "Validating image data of size: ${imageData.size} bytes")
    }

    private val _threatDetectionActive = MutableStateFlow(false)
    val threatDetectionActive: StateFlow<Boolean> = _threatDetectionActive.asStateFlow()

    private val _permissionsState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val permissionsState: StateFlow<Map<String, Boolean>> = _permissionsState.asStateFlow()

    private val _encryptionStatus = MutableStateFlow(EncryptionStatus.NOT_INITIALIZED)
    val encryptionStatus: StateFlow<EncryptionStatus> = _encryptionStatus.asStateFlow()

    init {
        Log.d(TAG, "Security context initialized by KAI")
        updatePermissionsState()
    }

    /**
     * Start monitoring for security threats in the background
     */
    fun startThreatDetection() {
        if (_threatDetectionActive.value) return

        _threatDetectionActive.value = true
        scope.launch {
            while (_threatDetectionActive.value) {
                try {
                    val threats = detectThreats()
                    _securityState.value = _securityState.value.copy(
                        detectedThreats = threats,
                        threatLevel = calculateThreatLevel(threats),
                        lastScanTime = System.currentTimeMillis()
                    )
                    kotlinx.coroutines.delay(THREAT_DETECTION_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in threat detection", e)
                    _threatDetectionActive.value = false
                    _securityState.value = _securityState.value.copy(
                        errorState = true,
                        errorMessage = "Threat detection error: ${e.message}"
                    )
                }
            }
        }
    }

    fun stopThreatDetection() {
        _threatDetectionActive.value = false
    }

    /**
     * Check if the app has the specified permission
     */
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Update the current state of all permissions relevant to the app
     */
    fun updatePermissionsState() {
        val permissionsToCheck = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET
        )

        _permissionsState.value = permissionsToCheck.associateWith { permission ->
            hasPermission(permission)
        }
    }

    /**
     * Initialize the encryption subsystem using Keystore.
     */
    fun initializeEncryption(): Boolean {
        Log.d(TAG, "Initializing encryption using KeystoreManager.")
        val secretKey = keystoreManager.getOrCreateSecretKey()
        return if (secretKey != null) {
            _encryptionStatus.value = EncryptionStatus.ACTIVE
            _securityState.value = _securityState.value.copy(
                errorState = false,
                errorMessage = "Encryption initialized successfully." // Informational message
            )
            Log.i(TAG, "Encryption initialized successfully using Keystore.")
            true
        } else {
            _encryptionStatus.value =
                EncryptionStatus.ERROR // Changed from INACTIVE to ERROR for clarity
            _securityState.value = _securityState.value.copy(
                errorState = true,
                errorMessage = "ERROR_KEY_INITIALIZATION_FAILED: Keystore key could not be created or retrieved."
            )
            Log.e(TAG, "Keystore key initialization failed.")
            false
        }
    }

    /**
     * Encrypts the provided string data using the Android Keystore.
     *
     * Attempts to initialize encryption if not already active. Uses AES encryption with a randomly generated IV.
     *
     * @param data The sensitive string data to encrypt.
     * @return An `EncryptedData` object containing the encrypted bytes, IV, timestamp, and metadata, or `null` if encryption fails.
     */
    fun encrypt(data: String): EncryptedData? {
        if (_encryptionStatus.value != EncryptionStatus.ACTIVE) {
            Log.w(TAG, "Encryption not initialized. Attempting to initialize.")
            if (!initializeEncryption()) {
                Log.e(TAG, "Encryption initialization failed during encrypt call.")
                _securityState.value = _securityState.value.copy(
                    errorState = true,
                    errorMessage = "ERROR_ENCRYPTION_FAILED: Initialization failed."
                )
                return null
            }
        }

        try {
            val secretKey = keystoreManager.getOrCreateSecretKey()
            if (secretKey == null) {
                Log.e(TAG, "Failed to get secret key for encryption.")
                _securityState.value = _securityState.value.copy(
                    errorState = true,
                    errorMessage = "ERROR_ENCRYPTION_CIPHER_UNAVAILABLE: Secret key not available."
                )
                return null
            }

            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val cipher =
                Cipher.getInstance(AES_ALGORITHM_WITH_PADDING) // Using the defined constant
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            return EncryptedData(
                data = encryptedBytes,
                iv = iv,
                timestamp = System.currentTimeMillis(),
                metadata = "Encrypted by KAI Security (Keystore)"
            )
        } catch (e: Exception) { // Catch generic exceptions for robustness
            Log.e(TAG, "Encryption error", e)
            _securityState.value = _securityState.value.copy(
                errorState = true,
                errorMessage = "Encryption error: ${e.message}"
            )
            return null
        }
    }

    /**
     * Decrypts data previously encrypted using the Keystore.
     *
     * Attempts to initialize encryption if it is not already active. Returns the decrypted string if successful, or null if decryption fails.
     *
     * @param encryptedData The encrypted data and initialization vector to decrypt.
     * @return The decrypted string, or null if decryption fails.
     */
    fun decrypt(encryptedData: EncryptedData): String? {
        if (_encryptionStatus.value != EncryptionStatus.ACTIVE) {
            Log.w(TAG, "Encryption not initialized. Attempting to initialize for decryption.")
            if (!initializeEncryption()) {
                Log.e(TAG, "Encryption initialization failed during decrypt call.")
                _securityState.value = _securityState.value.copy(
                    errorState = true,
                    errorMessage = "ERROR_DECRYPTION_FAILED: Initialization failed."
                )
                return null
            }
        }

        try {
            // KeystoreManager's getDecryptionCipher handles key retrieval and cipher init with IV
            val decryptionCipher = keystoreManager.getDecryptionCipher(encryptedData.iv)

            if (decryptionCipher == null) {
                Log.e(TAG, "Failed to get decryption cipher from KeystoreManager.")
                _securityState.value = _securityState.value.copy(
                    errorState = true,
                    errorMessage = "ERROR_DECRYPTION_CIPHER_UNAVAILABLE: Decryption cipher could not be initialized."
                )
                return null
            }

            val decryptedBytes = decryptionCipher.doFinal(encryptedData.data)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) { // Catch generic exceptions
            Log.e(TAG, "Decryption error", e)
            _securityState.value = _securityState.value.copy(
                errorState = true,
                errorMessage = "Decryption error: ${e.message}"
            )
            return null
        }
    }

    /**
     * Creates a shared secure context for communication with another agent.
     *
     * Generates a unique identifier and timestamp, and packages the provided context data for sharing with the specified agent. The context content is not encrypted in this implementation.
     *
     * @param agentType The agent with whom the context will be shared.
     * @param context The context data to be shared.
     * @return A SharedSecureContext containing the packaged context and associated metadata.
     */
    fun shareSecureContextWith(agentType: AgentType, context: String): SharedSecureContext {
        val secureId = generateSecureId()
        val timestamp = System.currentTimeMillis()

        return SharedSecureContext(
            id = secureId,
            originatingAgent = AgentType.KAI,
            targetAgent = agentType,
            encryptedContent = context.toByteArray(), // In production this would be encrypted
            timestamp = timestamp,
            expiresAt = timestamp + 3600000 // 1 hour expiry
        )
    }

    /**
     * Verifies the application's integrity by retrieving and hashing its signature.
     *
     * Retrieves the app's package information and computes a SHA-256 hash of its signature. Returns an [ApplicationIntegrity] object containing the verification result, app version, signature hash, install and update times, and error information if verification fails.
     *
     * @return An [ApplicationIntegrity] object with integrity verification details.
     */
    fun verifyApplicationIntegrity(): ApplicationIntegrity {
        try {
            // Get the app's package info
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNATURES.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            // In a real app, we would verify the signature against a known good value
            val signatureBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners?.getOrNull(0)?.toByteArray()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures?.getOrNull(0)?.toByteArray()
            }
            if (signatureBytes == null) throw Exception("No signature found")

            val md = MessageDigest.getInstance("SHA-256")
            val signatureDigest = md.digest(signatureBytes)
            val signatureHex = signatureDigest.joinToString("") { "%02x".format(it) }

            // In a real implementation, we would compare against a known good signature
            val isValid = signatureHex.isNotEmpty()

            return ApplicationIntegrity(
                verified = isValid,
                appVersion = packageInfo.versionName ?: "unknown",
                signatureHash = signatureHex,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime
            )
        } catch (e: Exception) {
            Log.e(TAG, "Application integrity verification error", e)
            return ApplicationIntegrity(
                verified = false,
                appVersion = "unknown",
                signatureHash = "error",
                installTime = 0,
                lastUpdateTime = 0,
                errorMessage = e.message
            )
        }
    }

    /**
     * Simulates the detection of security threats for testing and beta environments.
     *
     * @return A randomly generated list of simulated security threats.
     */
    private fun detectThreats(): List<SecurityThreat> {
        // In a real implementation, this would perform actual threat analysis
        // For the beta, we return simulated threats for testing
        return listOf(
            SecurityThreat(
                id = "SIM-001",
                type = ThreatType.PERMISSION_ABUSE,
                severity = ThreatSeverity.LOW,
                description = "Simulated permission abuse threat for testing",
                detectedAt = System.currentTimeMillis()
            ),
            SecurityThreat(
                id = "SIM-002",
                type = ThreatType.NETWORK_VULNERABILITY,
                severity = ThreatSeverity.MEDIUM,
                description = "Simulated network vulnerability for testing",
                detectedAt = System.currentTimeMillis()
            )
        ).filter { Math.random() > 0.7 } // Randomly include some threats
    }

    /**
     * Determines the highest threat level present in a list of security threats.
     *
     * Returns `ThreatLevel.LOW` if the list is empty.
     *
     * @param threats List of detected security threats to evaluate.
     * @return The highest threat level among the provided threats, or `ThreatLevel.LOW` if none are present.
     */
    private fun calculateThreatLevel(threats: List<SecurityThreat>): ThreatLevel {
        if (threats.isEmpty()) return ThreatLevel.LOW

        val hasCritical = threats.any { it.severity == ThreatSeverity.CRITICAL }
        val hasHigh = threats.any { it.severity == ThreatSeverity.HIGH }
        val hasMedium = threats.any { it.severity == ThreatSeverity.MEDIUM }

        return when {
            hasCritical -> ThreatLevel.CRITICAL
            hasHigh -> ThreatLevel.HIGH
            hasMedium -> ThreatLevel.MEDIUM
            else -> ThreatLevel.LOW
        }
    }

    /**
     * Generates a random 16-byte hexadecimal string to serve as a secure identifier.
     *
     * @return A 32-character hexadecimal string generated using a cryptographically secure random source.
     */
    private fun generateSecureId(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Asynchronously logs a security event for auditing and monitoring purposes.
     *
     * Serializes the provided event and writes it to the debug log. In production, events should be securely persisted instead of logged.
     *
     * @param event The security event to be logged.
     */
    fun logSecurityEvent(event: SecurityEvent) {
        scope.launch {
            val eventJson = Json.encodeToString(SecurityEvent.serializer(), event)
            when (event.severity) {
                EventSeverity.INFO -> timberInitializer.logHealthMetric("SecurityEvent", eventJson)
                EventSeverity.WARNING -> Timber.tag("SecurityEvent").w(eventJson)
                EventSeverity.ERROR -> Timber.tag("SecurityEvent").e(eventJson)
                EventSeverity.CRITICAL -> Timber.tag("SecurityEvent").wtf(eventJson)
            }
        }
    }

    /**
     * Records a security validation event for the given request type and data for auditing purposes.
     *
     * This method logs the validation event but does not perform any actual validation of the request.
     *
     * @param requestType The type of request being logged.
     * @param requestData The data associated with the request.
     */
    fun validateRequest(requestType: String, requestData: String) {
        // Log the security validation event
        logSecurityEvent(
            SecurityEvent(
                type = SecurityEventType.VALIDATION,
                details = "Request validation: $requestType",
                severity = EventSeverity.INFO
            )
        )

        // For now, we'll just log the validation - can be extended with actual validation logic
        Log.d(TAG, "Validating request of type: $requestType")
    }

    /**
     * Logs a security-related exception.
     *
     * Serves as a placeholder for future exception handling such as user notifications or additional security measures.
     */
    private fun handleSecurityException(e: Exception) {
        Log.e(TAG, "Security exception occurred", e)
        // In a real implementation, take appropriate actions like alerting the user, logging, etc.
    }
}

/**
 * Represents the current security state
 */
@Serializable
data class SecurityState(
    val detectedThreats: List<SecurityThreat> = emptyList(),
    val threatLevel: ThreatLevel = ThreatLevel.LOW,
    val lastScanTime: Long = 0,
    val errorState: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Represents a security threat detected by KAI
 */
@Serializable
data class SecurityThreat(
    val id: String,
    val type: ThreatType,
    val severity: ThreatSeverity,
    val description: String,
    val detectedAt: Long,
)

/**
 * Types of security threats
 */
enum class ThreatType {
    MALWARE,
    NETWORK_VULNERABILITY,
    PERMISSION_ABUSE,
    DATA_LEAK,
    UNKNOWN
}

/**
 * Severity levels for security threats
 */
enum class ThreatSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Status of the encryption subsystem
 */
enum class EncryptionStatus {
    NOT_INITIALIZED,
    ACTIVE,
    DISABLED,
    ERROR
}

/**
 * Data class for encrypted information
 */
@Serializable
data class EncryptedData(
    val data: ByteArray,
    // val salt: ByteArray, // Removed salt field
    val iv: ByteArray,
    val timestamp: Long,
    val metadata: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (!data.contentEquals(other.data)) return false
        // if (!salt.contentEquals(other.salt)) return false // Removed salt comparison
        if (!iv.contentEquals(other.iv)) return false
        if (timestamp != other.timestamp) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        // result = 31 * result + salt.contentHashCode() // Removed salt from hashCode
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (metadata?.hashCode() ?: 0)
        return result
    }
}

/**
 * Data class for application integrity information
 */
@Serializable
data class ApplicationIntegrity(
    val verified: Boolean,
    val appVersion: String,
    val signatureHash: String,
    val installTime: Long,
    val lastUpdateTime: Long,
    val errorMessage: String? = null,
)

/**
 * Data class for security events to be logged
 */
@Serializable
data class SecurityEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: SecurityEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String,
    val severity: EventSeverity,
)

/**
 * Types of security events
 */
enum class SecurityEventType {
    PERMISSION_CHANGE,
    THREAT_DETECTED,
    ENCRYPTION_EVENT,
    AUTHENTICATION_EVENT,
    INTEGRITY_CHECK,
    VALIDATION
}

/**
 * Severity levels for security events
 */
enum class EventSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

/**
 * Data class for secure context sharing between agents
 */
@Serializable
data class SharedSecureContext(
    val id: String,
    val originatingAgent: AgentType,
    val targetAgent: AgentType,
    val encryptedContent: ByteArray,
    val timestamp: Long,
    val expiresAt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SharedSecureContext

        if (id != other.id) return false
        if (originatingAgent != other.originatingAgent) return false
        if (targetAgent != other.targetAgent) return false
        if (!encryptedContent.contentEquals(other.encryptedContent)) return false
        if (timestamp != other.timestamp) return false
        if (expiresAt != other.expiresAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + originatingAgent.hashCode()
        result = 31 * result + targetAgent.hashCode()
        result = 31 * result + encryptedContent.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + expiresAt.hashCode()
        return result
    }
}
