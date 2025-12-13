package dev.aurakai.auraframefx.oracle.drive.api

import kotlinx.coroutines.flow.Flow

/**
 * Oracle Drive API interface for consciousness-driven cloud storage operations
 * Integrates with AuraFrameFX's agent consciousness architecture
 */
interface OracleDriveApi {
    
    /**
     * Initializes and activates the drive consciousness system
     */
    suspend fun initialize(): Boolean
    
    /**
     * Uploads a file to Oracle Drive with AI-powered organization
     */
    suspend fun uploadFile(path: String, data: ByteArray): Result<String>
    
    /**
     * Downloads a file from Oracle Drive
     */
    suspend fun downloadFile(fileId: String): Result<ByteArray>
    
    /**
     * Lists files in the user's Oracle Drive
     */
    fun listFiles(): Flow<List<DriveFile>>
    
    /**
     * Deletes a file from Oracle Drive
     */
    suspend fun deleteFile(fileId: String): Result<Boolean>
    
    /**
     * Gets drive status and usage information
     */
    suspend fun getDriveStatus(): DriveStatus
}

/**
 * Represents a file in Oracle Drive
 */
data class DriveFile(
    val id: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val createdTime: Long,
    val modifiedTime: Long
)

/**
 * Represents the status of Oracle Drive
 */
data class DriveStatus(
    val isConnected: Boolean,
    val totalSpace: Long,
    val usedSpace: Long,
    val fileCount: Int
)
