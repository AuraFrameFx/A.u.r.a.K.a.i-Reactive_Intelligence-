package dev.aurakai.auraframefx.oracledrive.genesis.cloud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.R.string.oracle_drive_not_connected
import dev.aurakai.auraframefx.app.viewmodel.OracleDriveControlViewModel
import kotlinx.coroutines.launch

/**
 * Displays the Oracle Drive control screen with UI controls and status information.
 */
@JvmOverloads
@Composable
fun OracleDriveControlScreen(
    viewModel: OracleDriveControlViewModel = viewModel(),
) {
    val failedToRefreshStr = stringResource(R.string.failed_to_refresh)
    val failedToToggleStr = stringResource(R.string.failed_to_toggle)

    // capture Android context only for things that truly need it (not used for resource formatting now)
    val isConnected by viewModel.isServiceConnected.collectAsState()
    val status by viewModel.status.collectAsState()
    val detailedStatus by viewModel.detailedStatus.collectAsState()
    val diagnosticsLog by viewModel.diagnosticsLog.collectAsState()
    var packageName by remember { mutableStateOf(TextFieldValue("")) }
    var enableModule by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val logScrollState = rememberScrollState()
    val viewModelScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.bindService()
        viewModel.refreshStatus()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.unbindService() }
    }

    // --- Helpers: safe wrappers that update UI state and call ViewModel ---
    fun safeRefresh() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.refreshStatus()
            } catch (_: Exception) {
                // avoid querying LocalContext inside coroutine; use captured stringResource values
                errorMessage = $$"$$failedToRefreshStr: ${e.localizedMessage ?: e.toString()}"
            } finally {
                isLoading = false
            }
        }
    }

    fun safeToggle() {
        if (packageName.text.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                viewModel.toggleModule(packageName.text, enableModule)
            } catch (_: Exception) {
                // avoid querying LocalContext inside coroutine; use captured stringResource values
                errorMessage = $$"$$failedToToggleStr: ${e.localizedMessage ?: e.toString()}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isConnected) stringResource(R.string.oracle_drive_connected) else stringResource(
                oracle_drive_not_connected
            ),
            style = MaterialTheme.typography.titleMedium,
            color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { safeRefresh() },
                enabled = isConnected && !isLoading
            ) {
                Text(stringResource(R.string.refresh_status))
            }
        }
        HorizontalDivider()
        Text(stringResource(R.string.status_label, status))
        Text(stringResource(R.string.detailed_status_label, detailedStatus))
        Text(
            stringResource(R.string.diagnostics_log_label),
            style = MaterialTheme.typography.labelMedium
        )
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = diagnosticsLog,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.verticalScroll(logScrollState)
            )
        }
        HorizontalDivider()
        Text(
            stringResource(R.string.toggle_module_label),
            style = MaterialTheme.typography.titleSmall
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                label = { Text(stringResource(R.string.module_package_name)) },
                enabled = isConnected && !isLoading
            )
            Switch(
                checked = enableModule,
                onCheckedChange = { enableModule = it },
                enabled = isConnected && !isLoading
            )
            Button(
                onClick = { safeToggle() },
                enabled = !(!isConnected || packageName.text.isBlank() || isLoading),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(stringResource(if (enableModule) R.string.enable else R.string.disable))
            }
        }
    }
}
