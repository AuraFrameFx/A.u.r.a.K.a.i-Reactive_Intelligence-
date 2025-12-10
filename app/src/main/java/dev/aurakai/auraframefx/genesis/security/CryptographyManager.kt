package dev.aurakai.auraframefx.genesis.security

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Minimal CryptographyManager used to satisfy DI and KSP during development.
 * Replace with full implementation that uses Android Keystore / Security library.
 */
@Singleton
class CryptographyManager @Inject constructor(private val context: Context) {

    fun generateSecureToken(): String = "genesis-stub-token"

    fun encrypt(data: ByteArray): ByteArray = data.map { (it + 1).toByte() }.toByteArray()

    fun decrypt(data: ByteArray): ByteArray = data.map { (it - 1).toByte() }.toByteArray()

}
