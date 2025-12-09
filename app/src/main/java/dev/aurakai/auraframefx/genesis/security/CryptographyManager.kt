package dev.aurakai.auraframefx.genesis.security

import android.content.Context

/**
 * CryptographyManager provides simple encryption/decryption operations.
 * NOTE: This is a development stub — replace with production crypto.
 */
interface CryptographyManager {
    fun encrypt(data: ByteArray, keyAlias: String = "default"): ByteArray
    fun decrypt(data: ByteArray, keyAlias: String = "default"): ByteArray
    fun removeKey(keyAlias: String)
    fun generateSecureToken(): String
}

/**
 * Simple (insecure) implementation used for local development and tests.
 */
class SimpleCryptographyManager(private val context: Context?) : CryptographyManager {
    override fun encrypt(data: ByteArray, keyAlias: String): ByteArray = data
    override fun decrypt(data: ByteArray, keyAlias: String): ByteArray = data
    override fun removeKey(keyAlias: String) {}
    override fun generateSecureToken(): String = "stub-token"

    companion object {
        fun getInstance(context: Context?): SimpleCryptographyManager = SimpleCryptographyManager(context)
    }
}
