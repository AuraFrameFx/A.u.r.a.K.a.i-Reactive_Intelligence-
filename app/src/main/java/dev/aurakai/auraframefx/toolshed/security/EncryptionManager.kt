package dev.aurakai.auraframefx.toolshed.security

/**
 * Simple EncryptionManager interface expected by SecureFileManager.
 * Keep API minimal for DI compatibility; production implementation should use Android keystore.
 */
interface EncryptionManager {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}

/** No-op implementation used during builds/tests. */
object NoopEncryptionManager : EncryptionManager {
    override fun encrypt(data: ByteArray): ByteArray = data
    override fun decrypt(data: ByteArray): ByteArray = data
}

