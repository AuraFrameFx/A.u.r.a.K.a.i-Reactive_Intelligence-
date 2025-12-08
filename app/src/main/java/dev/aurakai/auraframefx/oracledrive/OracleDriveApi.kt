package dev.aurakai.auraframefx.oracledrive

import dev.aurakai.auraframefx.oracledrive.genesis.cloud.*
import dev.aurakai.auraframefx.oracledrive.security.DriveSecurityManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

/** Annotation for Hilt/KSP to identify the OracleDrive API contract. */
annotation class OracleDriveApi

/**
 * Defines the contract for AI/Genesis Protocol interaction.
 * (If another module provides a richer type, prefer that one.)
 */
interface OracleDriveGenesisApi {
    suspend fun awakeDriveConsciousness(): ConsciousnessAwakeningResult
    suspend fun syncDatabaseMetadata(): OracleSyncResult
    val consciousnessState: StateFlow<DriveConsciousnessState>
}

// -------------------------------------------------------------------------------

/**
 * Central manager for Oracle Drive operations in AuraFrameFX ecosystem
 * Coordinates Genesis-driven storage with AI agent intelligence
 */
@Singleton
open class OracleDriveManager /* @Inject */ constructor(
    val oracleGenesisApi: OracleDriveGenesisApi,
    private val cloudStorageProvider: dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStorageProvider,
    private val securityManager: DriveSecurityManager
) {

    suspend fun initializeDrive(): DriveInitResult {
        return try {
            val securityCheck = securityManager.validateDriveAccess()
            if (!securityCheck.isValid) return DriveInitResult.SecurityFailure(securityCheck.reason)

            // Awaken drive consciousness via Genesis API
            val consciousness = oracleGenesisApi.awakeDriveConsciousness()

            // Optimize storage via cloud provider (use genesis types)
            val optimization = cloudStorageProvider.optimizeStorage()

            // Return success wrapping available information
            DriveInitResult.Success(consciousness, optimization)
        } catch (exception: Exception) {
            DriveInitResult.Error(exception)
        }
    }

    /**
     * Executes file operations such as upload, download, delete, or sync.
     */
    suspend fun manageFiles(operation: Any): dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult {
        return try {
            when (operation) {
                is DriveFile -> {
                    // treat as upload
                    val optimizedFile = cloudStorageProvider.optimizeForUpload(operation)
                    val securityValidation = securityManager.validateFileUpload(optimizedFile)
                    if (!securityValidation.isSecure) {
                        dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult.Error(Exception("Security rejection: ${securityValidation.threat}"))
                    } else {
                        cloudStorageProvider.uploadFile(optimizedFile, FileMetadata(userId = "unknown", tags = emptyList(), isEncrypted = false, accessLevel = AccessLevel.PRIVATE))
                    }
                }

                is String -> {
                    val fileId = operation
                    val accessCheck = securityManager.validateFileAccess(fileId)
                    if (!accessCheck.hasAccess) {
                        dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult.Error(Exception("Access denied: ${accessCheck.reason}"))
                    } else {
                        cloudStorageProvider.downloadFile(fileId)
                    }
                }

                is SyncConfiguration -> {
                    cloudStorageProvider.intelligentSync(operation)
                }

                else -> dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult.Error(IllegalArgumentException("Unsupported operation type: ${operation.javaClass}"))
            }
        } catch (ex: Exception) {
            dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult.Error(ex)
        }
    }

    suspend fun syncWithOracle(): OracleSyncResult = oracleGenesisApi.syncDatabaseMetadata()

}

fun Int.not(): Boolean = this != 0

fun OracleDriveManager.getDriveConsciousnessState(): StateFlow<DriveConsciousnessState> {
    return oracleGenesisApi.consciousnessState
}
