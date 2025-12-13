package dev.aurakai.auraframefx.oracle.drive.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of OracleDriveApi for build compatibility
 */
@Singleton
class OracleDriveApiImpl @Inject constructor() : OracleDriveApi {
    
    override suspend fun initialize(): Boolean {
        // Stub - implement actual initialization
        return true
    }
    
    override suspend fun uploadFile(path: String, data: ByteArray): Result<String> {
        // Stub - implement actual upload
        return Result.success("stub-file-id")
    }
    
    override suspend fun downloadFile(fileId: String): Result<ByteArray> {
        // Stub - implement actual download
        return Result.success(ByteArray(0))
    }
    
    override fun listFiles(): Flow<List<DriveFile>> {
        // Stub - implement actual file listing
        return flowOf(emptyList())
    }
    
    override suspend fun deleteFile(fileId: String): Result<Boolean> {
        // Stub - implement actual deletion
        return Result.success(true)
    }
    
    override suspend fun getDriveStatus(): DriveStatus {
        // Stub - implement actual status retrieval
        return DriveStatus(
            isConnected = false,
            totalSpace = 0L,
            usedSpace = 0L,
            fileCount = 0
        )
    }
}
