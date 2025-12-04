package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Placeholder LSPosed / Xposed gate screen - replace with full implementation later
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LSPosedGateScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("LSPosed / Xposed") })
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LSPosed / Xposed - coming soon", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Module manager, hooks, logs, and quick actions will be available here.")
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onNavigateBack) {
                    Text("Back")
                }
            }
        }
    }
}
