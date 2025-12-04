package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Main screen of the AuraFrameFX application.
 * This is a stub implementation - replace with actual UI.
 */
@Composable
fun MainScreen(
    onNavigateToAgentNexus: () -> Unit = {},
    onNavigateToOracleDrive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AuraFrameFX",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateToAgentNexus) {
            Text("Agent Nexus")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToOracleDrive) {
            Text("Oracle Drive")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToSettings) {
            Text("Settings")
        }
    }
}
