package com.example.app.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.util.Log // Kept standard Log for legacy error handling, but Timber is used for primary logging
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Removed: import androidx.work.impl.utils.forTag
import com.example.app.ipc.IAuraDriveService
// Removed: import com.google.firebase.database.core.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// Removed: import okhttp3.internal.platform.AndroidPlatform.Companion.Tag
import timber.log.Timber // ADDED/CONFIRMED Timber import
import javax.inject.Inject

/**
 * ViewModel for Oracle Drive Control Screen
 *
 * This ViewModel manages the UI state and business logic for the Oracle Drive feature,
 * including file operations with R.G.S.F. memory integrity verification.
 */
@HiltViewModel
class OracleDriveControlViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val TAG = "OracleDriveVM"

    // Service connection state
    private var auraDriveService: IAuraDriveService? = null
    private var isBound = false

    // UI State
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val _status = MutableStateFlow("Initializing Oracle Drive...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _detailedStatus = MutableStateFlow("")
    val detailedStatus: StateFlow<String> = _detailedStatus.asStateFlow()

    private val _diagnosticsLog = MutableStateFlow("")
    val diagnosticsLog: StateFlow<String> = _diagnosticsLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Service connection
    private val connection = object : ServiceConnection {
        /**
         * Handles actions when the AuraDriveService is connected.
         *
         * Sets up the service interface, marks the service as bound, updates the connection state, and triggers a status refresh.

         */
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // FIX: Using the defined TAG variable instead of the unresolved Tag() function
            Timber.tag(TAG).d("Service connected")
            auraDriveService = with(service) {
                IAuraDriveService.Stub.asInterface()
            }
            isBound = true
            _isServiceConnected.value = true
            refreshStatus()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Timber.d("Service disconnected")
            isBound = false
            _isServiceConnected.value = false
            _status.value = "Service disconnected"
        }
    }

    init {
        bindService()
    }

    /**
     * Initiates binding to the AuraDriveService for Oracle Drive operations.
     *
     * Updates the status to indicate connection progress. If binding fails, sets an error message.

     */
    fun bindService() {
        try {
            val intent =
                Intent(context, Class.forName("dev.aurakai.auraframefx.services.AuraDriveService"))
            context.bindService(
                intent,
                connection,
                Context.BIND_AUTO_CREATE
            )
            _status.value = "Connecting to Oracle Drive..."
        } catch (e: Exception) {
            Log.e(TAG, "Error binding to service", e)
            _errorMessage.value = "Failed to connect to Oracle Drive: ${e.message}"
        }
    }

    /**
     * Unbinds from the AuraDriveService
     */
    fun unbindService() {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
            _isServiceConnected.value = false
            _status.value = "Disconnected from Oracle Drive"
        }
    }

    /**
     * Updates the UI state with the latest status, detailed status, and diagnostics log from the Oracle Drive service.
     *
     * Fetches current status information and diagnostics from the bound service and updates the corresponding state flows. Sets an error message if the operation fails.

     */
    fun refreshStatus() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val service =
                    auraDriveService ?: throw IllegalStateException("Service not connected")

                _status.value = service.getOracleDriveStatus() ?: "Status unavailable"
                _detailedStatus.value =
                    service.getDetailedInternalStatus() ?: "Detailed status unavailable"

                val logs = service.getInternalDiagnosticsLog()
                _diagnosticsLog.value = logs?.joinToString("\n") ?: "No diagnostic logs available"

                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing status", e)
                _errorMessage.value = "Failed to refresh status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Imports a file from the given URI using the AuraDriveService.
     *
     * Initiates an asynchronous import operation and updates UI state flows with the result or error message.

     *
     * @param uri The URI of the file to import.
     */
    fun importFile(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val service =
                    auraDriveService ?: throw IllegalStateException("Service not connected")

                val fileId = service.importFile(uri)
                _status.value = "File imported successfully (ID: $fileId)"
                refreshStatus()

            } catch (e: Exception) {
                Log.e(TAG, "Error importing file", e)
                _errorMessage.value = "Import failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Exports a file identified by its ID to the specified destination URI using the AuraDriveService.
     *
     * Updates the status and error message state flows based on the result of the export operation.
     *
     * @param fileId The unique identifier of the file to export.
     * @param destinationUri The URI where the file will be exported.

     */
    fun exportFile(fileId: String, destinationUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val service =
                    auraDriveService ?: throw IllegalStateException("Service not connected")

                val success = service.exportFile(fileId, destinationUri)
                if (success) {
                    _status.value = "File exported successfully"
                    refreshStatus()
                } else {
                    _errorMessage.value = "Export operation failed"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error exporting file", e)
                _errorMessage.value = "Export failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Initiates verification of the integrity of a file via the AuraDriveService.
     *
     * Updates UI state flows to reflect verification results or errors.
     *
     * @param fileId The identifier of the file to verify.

     */
    fun verifyFileIntegrity(fileId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val service =
                    auraDriveService ?: throw IllegalStateException("Service not connected")

                val isValid = service.verifyFileIntegrity(fileId)
                if (isValid) {
                    _status.value = "File integrity verified successfully"
                } else {
                    _errorMessage.value = "File integrity verification failed"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error verifying file integrity", e)
                _errorMessage.value = "Verification failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Enables or disables a module identified by its package name via the AuraDrive service.
     *
     * Updates the status or error message state flows based on the operation result and refreshes the current status on success.
     *
     * @param packageName The package name of the module to toggle.
     * @param enable `true` to enable the module, `false` to disable it.

     */
    fun toggleModule(packageName: String, enable: Boolean) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val service =
                    auraDriveService ?: throw IllegalStateException("Service not connected")

                val result = service.toggleLSPosedModule(packageName, enable)
                // toggleLSPosedModule returns String, check if it indicates success
                val isSuccess = if (result.contains("success", ignoreCase = true) || result.contains(
                        "enabled",
                        ignoreCase = true
                    ) || result.contains("disabled", ignoreCase = true)
                ) true else false
                if (isSuccess) {
                    val action = if (enable) "enabled" else "disabled"
                    _status.value = "Module '$packageName' $action successfully: $result"
                    refreshStatus()
                } else {
                    _errorMessage.value = "Failed to toggle module '$packageName': $result"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error toggling module", e)
                _errorMessage.value = "Module operation failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Called when the ViewModel is being destroyed to perform cleanup, including unbinding from the AuraDriveService.

     */
    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}
