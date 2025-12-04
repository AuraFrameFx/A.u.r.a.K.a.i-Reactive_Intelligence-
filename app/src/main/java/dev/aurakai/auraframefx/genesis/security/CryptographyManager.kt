package dev.aurakai.auraframefx.genesis.security

/**
 * Manages cryptographic operations for the application.
 */
interface CryptographyManager {
    /**
     * Encrypts the given data using the specified key alias.
     * @param data The data to encrypt
     * @param keyAlias The alias of the key to use for encryption
     * @return The encrypted data
     */
    fun encrypt(data: ByteArray, keyAlias: String): ByteArray

    /**
     * Decrypts the given data using the specified key alias.
     * @param data The data to decrypt
     * @param keyAlias The alias of the key to use for decryption
     * @return The decrypted data
     */
    fun decrypt(data: ByteArray, keyAlias: String): ByteArray

    /**
     * Removes the key with the specified alias.
     * @param keyAlias The alias of the key to remove
     */
    fun removeKey(keyAlias: String)

    /**
     * Generates a secure token for authentication.
     * @return A secure token string
     */
    fun generateSecureToken(): String
}
