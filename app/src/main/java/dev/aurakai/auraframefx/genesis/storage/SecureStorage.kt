// Minimal SecureStorage stub to satisfy DI and KSP
package dev.aurakai.auraframefx.genesis.storage

import android.content.Context

/**
 * A minimal secure storage API used for DI resolution during development.
 * This implementation stores values in-memory for local builds.
 */
class SecureStorage private constructor() {
    private val map = mutableMapOf<String, String>()

    fun encrypt(key: String, value: String) {
        map[key] = value
    }

    fun decrypt(key: String): String? = map[key]

    fun remove(key: String) {
        map.remove(key)
    }

    companion object {
        fun getInstance(context: Context?): SecureStorage = SecureStorage()
    }
}

