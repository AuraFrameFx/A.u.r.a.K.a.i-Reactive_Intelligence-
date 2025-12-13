package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.ui.AuraMoodViewModel // Corrected import

// TODO: Function reported as unused or needs implementation.
@Composable
fun AurakaiEcoSysScreen(
    // viewModel: AuraMoodViewModel = hiltViewModel() // Alternative injection for previews if needed
    // TODO: Parameter onBack reported as unused in original error (if it was present)
) {
    val viewModel: AuraMoodViewModel = hiltViewModel()
    val currentMood by viewModel.moodState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Aurakai Ecosystem",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Current Mood from ViewModel: ${currentMood.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.onUserInput("happy") }) {
                Text("Make Aura Happy")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.onUserInput("sad") }) {
                Text("Make Aura Sad")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AurakaiEcoSysScreenPreview() {
    MaterialTheme {
        // In Previews, hiltViewModel() won't work directly without extra setup.
        // For a simple preview, you might pass a mocked/stubbed ViewModel instance
        // or use a simpler Composable that doesn't rely on the ViewModel for basic layout checks.
        // AurakaiEcoSysScreen() // This will error in preview if hiltViewModel() is directly called

        // Simple preview of the text part:
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Aurakai Ecosystem Screen (Placeholder)")
            Text(text = "Current Mood from ViewModel: NEUTRAL")
        }
    }
}
