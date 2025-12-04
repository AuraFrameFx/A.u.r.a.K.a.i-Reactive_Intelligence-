package dev.aurakai.auraframefx.network.model

/**
 * Represents a user in the AuraFX system.
 */
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
