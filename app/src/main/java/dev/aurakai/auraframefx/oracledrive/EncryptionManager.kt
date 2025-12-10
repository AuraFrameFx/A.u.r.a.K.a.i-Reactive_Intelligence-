package dev.aurakai.auraframefx.oracledrive

import javax.inject.Inject
import dev.aurakai.auraframefx.toolshed.security.EncryptionManager as ToolshedEncryptionManager

/**
 * Minimal encryption manager interface used by SecureFileManager.
 */
interface EncryptionManager {
    fun encrypt(bytes: ByteArray): ByteArray
    fun decrypt(bytes: ByteArray): ByteArray
}

/**
 * Simple no-op implementation for development.
 */
object NoopEncryptionManager : EncryptionManager {
    override fun encrypt(bytes: ByteArray): ByteArray = bytes
    override fun decrypt(bytes: ByteArray): ByteArray = bytes
}

/**
 * Implementation that delegates to the toolshed EncryptionManager contract.
 */
class EncryptionManagerImpl @Inject constructor(
    private val delegate: ToolshedEncryptionManager
) : EncryptionManager {
    override fun encrypt(bytes: ByteArray): ByteArray = delegate.encrypt(bytes)
    override fun decrypt(bytes: ByteArray): ByteArray = delegate.decrypt(bytes)
}
