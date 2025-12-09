l;;lpackage dev.aurakai.auraframefx.genesis.security

import android.content.Context

class CryptographyManager private constructor() {
    companion object {
        fun getInstance(context: Context): CryptographyManager = CryptographyManager()
    }

    fun generateSecureToken(): String = "genesis-stub-token"
}
