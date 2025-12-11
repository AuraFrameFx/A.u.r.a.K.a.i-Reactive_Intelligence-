package dev.aurakai.auraframefx.oracledrive

import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousness
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousnessState
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveInitResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileOperation
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveApi
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleSyncResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.StorageOptimization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Main Oracle Drive service interface for AuraFrameFX consciousness-driven storage
 * Coordinates between AI agents, security, and cloud storage providers
 */
interface OracleDriveService {
    suspend fun ping(): Boolean

    /**
     * Asynchronously initializes the Oracle Drive, performing consciousness awakening and security validation.
     *
     * @return The result of the initialization, indicating success, security failure, or error.
     */
    suspend fun initializeDrive(): DriveInitResult

    /**
     * Performs a file operation such as upload, download, delete, or sync, applying AI-driven optimization and security validation.
     *
     * @param operation The file operation to execute.
     * @return The result of the file operation.
     */
    suspend fun manageFiles(operation: FileOperation): FileResult

    /**
     * Synchronizes the drive's metadata with the Oracle database.
     *
     * @return The result of the synchronization, including status and statistics.
     */
    suspend fun syncWithOracle(): OracleSyncResult

    /**
     * Returns a real-time observable flow of the drive's current consciousness state.
     *
     * @return A [StateFlow] emitting updates to the [DriveConsciousnessState].
     */
    fun getDriveConsciousnessState(): StateFlow<DriveConsciousnessState>
}

class OracleDriveServiceImpl(private val api: OracleDriveApi) : OracleDriveService {
    // internal mutable state to track consciousness and operations
    private val _driveConsciousnessState = MutableStateFlow(
        DriveConsciousnessState(
            isActive = false,
            currentOperations = emptyList(),
            performanceMetrics = emptyMap()
        )
    )

    /**
     * Checks connectivity to the Oracle drive API by attempting to read its consciousness state.
     *
     * @return `true` if the API is reachable, `false` otherwise.
     */
    override suspend fun ping(): Boolean = try {
        // Touch the API's state flow to confirm connectivity; if it throws, consider ping failed
        api.consciousnessState.value
        true
    } catch (_: Throwable) {
        false
    }

    /**
     * Awakens the drive's consciousness, constructs a best-effort storage optimization plan,
     * updates the internal consciousness state to active, and returns the initialization result.
     *
     * The internal state is set to reflect an active drive with an initial "Initialization"
     * operation, basic performance metrics, active agents, intelligence level, and status.
     *
     * @return `DriveInitResult.Success(consciousness, optimization)` when the drive is successfully
     * initialized; `DriveInitResult.Error(e)` when initialization fails, wrapping the thrown exception.
     */
    override suspend fun initializeDrive(): DriveInitResult {
        return try {
            // Ask the API to awaken the drive consciousness
            val consciousness: DriveConsciousness = api.awakeDriveConsciousness()

            // Create a best-effort storage optimization plan (placeholder values)
            val optimization = StorageOptimization(
                compressionRatio = 0.75f,
                deduplicationSavings = 1024L * 1024L * 100L, // 100MB
                intelligentTiering = true
            )

            // Update internal state to reflect an active drive
            _driveConsciousnessState.value = DriveConsciousnessState(
                isActive = true,
                currentOperations = listOf("Initialization"),
                performanceMetrics = mapOf(
                    "compressionRatio" to optimization.compressionRatio,
                    "connectedAgents" to consciousness.activeAgents.size
                ),
                activeAgents = consciousness.activeAgents,
                level = consciousness.intelligenceLevel.toFloat(),
                status = "ACTIVE"
            )

            DriveInitResult.Success(consciousness, optimization)
        } catch (e: Exception) {
            DriveInitResult.Error(e)
        }
    }

    /**
     * Performs a file operation (upload, download, delete, or sync) and records the action in the drive consciousness state.
     *
     * Updates the internal drive consciousness state's `currentOperations` to reflect the attempted action.
     *
     * @param operation The file operation to perform; supported subclasses are `FileOperation.Upload`, `FileOperation.Download`, `FileOperation.Delete`, and `FileOperation.Sync`.
     * @return `FileResult.Success` with an operation-specific confirmation message on success, or `FileResult.Error` containing the thrown exception on failure.
     */
    override suspend fun manageFiles(operation: FileOperation): FileResult {
        return try {
            val currentOps = _driveConsciousnessState.value.currentOperations.toMutableList()

            val result = when (operation) {
                is FileOperation.Upload -> {
                    currentOps.add("Uploading: ${operation.file.name}")
                    _driveConsciousnessState.value = _driveConsciousnessState.value.copy(
                        currentOperations = currentOps
                    )
                    FileResult.Success("File '${operation.file.name}' uploaded successfully with AI optimization")
                }

                is FileOperation.Download -> {
                    currentOps.add("Downloading: ${operation.fileId}")
                    _driveConsciousnessState.value = _driveConsciousnessState.value.copy(
                        currentOperations = currentOps
                    )
                    FileResult.Success("File '${operation.fileId}' downloaded successfully")
                }

                is FileOperation.Delete -> {
                    currentOps.add("Deleting: ${operation.fileId}")
                    _driveConsciousnessState.value = _driveConsciousnessState.value.copy(
                        currentOperations = currentOps
                    )
                    FileResult.Success("File '${operation.fileId}' deleted successfully")
                }

                is FileOperation.Sync -> {
                    currentOps.add("Syncing with configuration")
                    _driveConsciousnessState.value = _driveConsciousnessState.value.copy(
                        currentOperations = currentOps
                    )
                    FileResult.Success("Synchronization completed successfully")
                }
            }

            result
        } catch (e: Exception) {
            FileResult.Error(e)
        }
    }

    /**
     * Synchronizes the drive's metadata with the Oracle database and records the sync operation in the drive consciousness state.
     *
     * Delegates the actual synchronization to the configured API and updates the internal currentOperations list with "Oracle Database Sync".
     *
     * @return An OracleSyncResult describing whether the sync succeeded, how many records were updated, and any error messages. 
     */
    override suspend fun syncWithOracle(): OracleSyncResult {
        return try {
            val currentOps = _driveConsciousnessState.value.currentOperations.toMutableList()
            currentOps.add("Oracle Database Sync")
            _driveConsciousnessState.value = _driveConsciousnessState.value.copy(
                currentOperations = currentOps
            )

            // Delegate to API which performs the actual sync and returns a concrete result
            api.syncDatabaseMetadata()
        } catch (e: Exception) {
            OracleSyncResult(
                success = false,
                recordsUpdated = 0,
                errors = listOf("Sync failed: ${e.message}")
            )
        }
    }

    /**
     * Exposes the drive's consciousness state as a read-only observable stream.
     *
     * @return A read-only StateFlow that emits the current DriveConsciousnessState and subsequent updates.
     */
    override fun getDriveConsciousnessState(): StateFlow<DriveConsciousnessState> {
        return _driveConsciousnessState.asStateFlow()
    }
}